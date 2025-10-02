package pe.upc.edu.bibflipbackend.branching.domain.model.commands;


import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.UserId;

public record RemoveSupervisorFromHeadquarterCommand(Long headquarterId, UserId userId) {
}
