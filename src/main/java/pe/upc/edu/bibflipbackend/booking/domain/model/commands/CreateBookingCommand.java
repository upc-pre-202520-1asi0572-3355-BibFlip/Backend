package pe.upc.edu.bibflipbackend.booking.domain.model.commands;

import java.time.LocalDate;
import java.util.List;

public record CreateBookingCommand(
        Long clientId,
        Long cubicleId,
        LocalDate bookingDate,
        List<Long> slotIds
) {
}
