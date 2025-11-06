package pe.upc.edu.bibflipbackend.booking.domain.model.commands;

import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.CubicleStatus;

public record UpdateCubicleStatusCommand(Long cubicleId, CubicleStatus status) {
}
