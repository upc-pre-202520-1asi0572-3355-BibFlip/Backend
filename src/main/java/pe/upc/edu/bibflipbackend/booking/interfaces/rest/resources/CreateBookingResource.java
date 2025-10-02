package pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

public record CreateBookingResource(
        Long clientId,
        Long cubicleId,
        LocalDate bookingDate,
        List<Long> slotIds
) {
}
