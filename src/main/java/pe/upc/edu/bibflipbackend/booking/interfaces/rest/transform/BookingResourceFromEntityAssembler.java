package pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Booking;
import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Cubicle;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.BookingResource;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.BookingSlotResource;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class BookingResourceFromEntityAssembler {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static BookingResource toResourceFromEntity(Booking booking) {
        List<BookingSlotResource> slotResources = booking.getBookingSlots().stream()
                .map(slot -> new BookingSlotResource(
                        slot.getTimeInterval().startTime().format(TIME_FORMATTER),
                        slot.getTimeInterval().endTime().format(TIME_FORMATTER)))
                .collect(Collectors.toList());

        // Obtener la tabla y acceder a headquarters correctamente
        Cubicle cubicle = booking.getCubicleId();
        Long headquarterId = cubicle.getHeadquarterId().headquarterId();

        return new BookingResource(
                booking.getId(),
                booking.getUserId().clientId(),// assuming UserId has a getValue() method
                booking.getCubicleId().getCubicleDetails().cubicleNumber().longValue(),
                headquarterId,
                booking.getCubicleId().getId(),       // assuming Cubicle has a getId() method
                booking.getBookingDate(),
                slotResources
        );
    }
}
