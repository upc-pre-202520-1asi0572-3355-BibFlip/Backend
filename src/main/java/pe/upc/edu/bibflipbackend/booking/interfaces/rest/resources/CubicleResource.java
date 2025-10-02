package pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources;

public record CubicleResource(
        Long id,
        Long headquarterId,
        Integer cubicleNumber,
        Integer seats,
        String status,
        String zone
) {
}
