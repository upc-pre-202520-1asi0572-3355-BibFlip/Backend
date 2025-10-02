package pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform;

import pe.upc.edu.bibflipbackend.booking.domain.model.commands.CreateBookingCommand;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.CreateBookingResource;

public class CreateBookingCommandFromResourceAssembler {
    public static CreateBookingCommand toCommandFromResource(CreateBookingResource resource) {
        return new CreateBookingCommand(
                resource.clientId(),
                resource.cubicleId(),
                resource.bookingDate(),
                resource.slotIds()
        );
    }
}
