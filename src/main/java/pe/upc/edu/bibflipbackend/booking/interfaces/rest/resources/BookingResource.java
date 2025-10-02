package pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.List;

public record BookingResource(
        Long id,
        Long clientId,
        Long cubicleNumber,
        Long headquarterId,
        Long cubicleId,
        LocalDate bookingDate,
        List<BookingSlotResource> bookingSlots
) {}
