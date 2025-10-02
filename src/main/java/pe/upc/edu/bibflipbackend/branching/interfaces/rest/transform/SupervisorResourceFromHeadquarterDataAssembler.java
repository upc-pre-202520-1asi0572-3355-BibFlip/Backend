package pe.upc.edu.bibflipbackend.branching.interfaces.rest.transform;

import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.HeadquarterData;
import pe.upc.edu.bibflipbackend.branching.interfaces.rest.resources.SupervisorResource;

public class SupervisorResourceFromHeadquarterDataAssembler {

    public static SupervisorResource toResourceFromEntity(HeadquarterData data) {
        return new SupervisorResource(
                data.userId(),
                data.username(),
                data.headquarterId()
        );
    }
}