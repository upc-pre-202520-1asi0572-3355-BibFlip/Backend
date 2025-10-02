package pe.upc.edu.bibflipbackend.booking.domain.services;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Cubicle;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.CreateCubicleCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.CreateCubicleScheduleCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.DeleteCubicleCommand;

import java.util.Optional;

public interface CubicleCommandService {
    Optional<Cubicle> handle(CreateCubicleCommand command);
    Optional<Cubicle> handle(CreateCubicleScheduleCommand command);
    void handle(DeleteCubicleCommand command);
}
