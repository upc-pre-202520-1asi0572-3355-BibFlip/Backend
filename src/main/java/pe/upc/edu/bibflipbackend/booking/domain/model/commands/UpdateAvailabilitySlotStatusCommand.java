package pe.upc.edu.bibflipbackend.booking.domain.model.commands;

import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.ScheduleSlotStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateAvailabilitySlotStatusCommand(
        Long cubicleId,
        LocalDate date,
        LocalTime currentTime,
        ScheduleSlotStatus status
) { }
