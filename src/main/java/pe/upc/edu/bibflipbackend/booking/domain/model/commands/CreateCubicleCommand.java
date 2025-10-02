package pe.upc.edu.bibflipbackend.booking.domain.model.commands;

public record CreateCubicleCommand(Integer cubicleNumber, Integer seats, Long headquarterId, String zone) {

}
