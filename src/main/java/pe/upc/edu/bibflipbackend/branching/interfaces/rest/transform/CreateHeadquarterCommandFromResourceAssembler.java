package pe.upc.edu.bibflipbackend.branching.interfaces.rest.transform;

import pe.upc.edu.bibflipbackend.branching.domain.model.commands.CreateHeadquarterCommand;
import pe.upc.edu.bibflipbackend.branching.interfaces.rest.resources.CreateHeadquarterResource;

import java.time.LocalTime;

public class CreateHeadquarterCommandFromResourceAssembler {
    public static CreateHeadquarterCommand toCommandFromResource(CreateHeadquarterResource resource) {
        return new CreateHeadquarterCommand(
                resource.name(),
                resource.landlinePhone(), resource.mobilePhone(),
                resource.latitude(), resource.longitude(),
                resource.street(), resource.number(), resource.city(), resource.postalCode(), resource.country(),
                LocalTime.parse(resource.openingTime()),  // Parse from String
                LocalTime.parse(resource.closingTime()),  // Parse from String
                resource.intervalMinutes()
        );
    }
}
