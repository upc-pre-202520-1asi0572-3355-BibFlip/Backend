package pe.upc.edu.bibflipbackend.branching.interfaces.rest.transform;

import pe.upc.edu.bibflipbackend.branching.domain.model.aggregates.Headquarter;
import pe.upc.edu.bibflipbackend.branching.interfaces.rest.resources.HeadquarterResource;

import java.time.format.DateTimeFormatter;

public class HeadquarterResourceFromEntityAssembler {
    public static HeadquarterResource toResourceFromEntity(Headquarter entity) {
        return new HeadquarterResource(
                entity.getId(),
                entity.getName().name(),
                entity.getContactNumbers().landlinePhone(), entity.getContactNumbers().mobilePhone(),
                entity.getCoordinates().latitude(), entity.getCoordinates().longitude(),
                entity.getAddress().getAddress(),
                entity.getOpeningTime().format(DateTimeFormatter.ofPattern("HH:mm")), // Formatea aquí
                entity.getClosingTime().format(DateTimeFormatter.ofPattern("HH:mm")), // Formatea aquí
                entity.getIntervalMinutes()
        );
    }
}
