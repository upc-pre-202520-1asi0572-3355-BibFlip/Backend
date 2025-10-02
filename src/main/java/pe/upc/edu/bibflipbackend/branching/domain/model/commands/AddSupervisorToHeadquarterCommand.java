package pe.upc.edu.bibflipbackend.branching.domain.model.commands;

import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.UserId;

public record AddSupervisorToHeadquarterCommand(Long headquarterId, UserId userId) {
}
