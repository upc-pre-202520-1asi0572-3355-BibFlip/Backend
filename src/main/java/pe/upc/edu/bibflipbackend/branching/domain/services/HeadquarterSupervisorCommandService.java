package pe.upc.edu.bibflipbackend.branching.domain.services;

import pe.upc.edu.bibflipbackend.branching.domain.model.commands.AddSupervisorToHeadquarterCommand;
import pe.upc.edu.bibflipbackend.branching.domain.model.commands.RemoveSupervisorFromHeadquarterCommand;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.HeadquarterData;

import java.util.Optional;

public interface HeadquarterSupervisorCommandService {
    Optional<HeadquarterData> handle(AddSupervisorToHeadquarterCommand command);
    Optional<HeadquarterData> handle(RemoveSupervisorFromHeadquarterCommand command);
}
