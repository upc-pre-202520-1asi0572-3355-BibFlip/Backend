package pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Cubicle;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.CubicleResource;

public class CubicleResourceFromEntityAssembler {
    public static CubicleResource toResourceFromEntity(Cubicle entity){
        return new CubicleResource(
                entity.getId(),
                entity.getHeadquarterId().headquarterId(),
                entity.getCubicleDetails().cubicleNumber(),
                entity.getCubicleDetails().seats(),
                entity.getStatus().toString(),
                entity.getZone().getName()
        );
    }
}
