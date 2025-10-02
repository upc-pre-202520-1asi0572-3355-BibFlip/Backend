package pe.upc.edu.bibflipbackend.branching.domain.services;

import pe.upc.edu.bibflipbackend.branching.domain.model.aggregates.Headquarter;
import pe.upc.edu.bibflipbackend.branching.domain.model.commands.CreateHeadquarterCommand;

import java.util.Optional;

public interface HeadquarterCommandService {
    Optional<Headquarter> handle(CreateHeadquarterCommand command);
}
