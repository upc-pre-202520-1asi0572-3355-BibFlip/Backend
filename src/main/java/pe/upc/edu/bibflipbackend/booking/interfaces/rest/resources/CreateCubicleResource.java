package pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources;

public record CreateCubicleResource(
        Long headquarterId,
        Integer cubicleNumber,
        Integer seats,
        String zone
) {
}
