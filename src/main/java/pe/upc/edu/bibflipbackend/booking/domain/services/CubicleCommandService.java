package pe.upc.edu.bibflipbackend.booking.domain.services;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Cubicle;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.*;
import pe.upc.edu.bibflipbackend.booking.domain.model.entities.AvailabilitySlot;

import java.util.Optional;

public interface CubicleCommandService {
    Optional<Cubicle> handle(CreateCubicleCommand command);
    Optional<Cubicle> handle(CreateCubicleScheduleCommand command);
    void handle(DeleteCubicleCommand command);
    Optional<Cubicle> handle(UpdateCubicleStatusCommand command);
    Optional<AvailabilitySlot> handle(UpdateAvailabilitySlotStatusCommand command);
}
