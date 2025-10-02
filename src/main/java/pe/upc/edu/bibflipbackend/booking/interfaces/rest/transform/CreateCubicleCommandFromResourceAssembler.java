package pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform;

import pe.upc.edu.bibflipbackend.booking.domain.model.commands.CreateCubicleCommand;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.CreateCubicleResource;

public class CreateCubicleCommandFromResourceAssembler {
    public static CreateCubicleCommand toCommandFromResource(CreateCubicleResource resource) {
        return new CreateCubicleCommand(
                resource.cubicleNumber(),
                resource.seats(),
                resource.headquarterId(),
                resource.zone()
        );
    }
}
