package pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources;

import java.time.LocalDate;

public record AvailabilitySlotResource(
        Long id,
        LocalDate date,
        String startTime,
        String endTime,
        String status
) {
}