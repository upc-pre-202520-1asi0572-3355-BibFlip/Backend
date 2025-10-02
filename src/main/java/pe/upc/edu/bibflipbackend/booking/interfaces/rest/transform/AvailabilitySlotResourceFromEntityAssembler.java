package pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform;

import pe.upc.edu.bibflipbackend.booking.domain.model.entities.AvailabilitySlot;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.AvailabilitySlotResource;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AvailabilitySlotResourceFromEntityAssembler {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static AvailabilitySlotResource toResourceFromEntity(AvailabilitySlot entity) {
        return new AvailabilitySlotResource(
                entity.getId(),
                entity.getDateOfSlot(),
                entity.getTimeInterval().startTime().format(TIME_FORMATTER),
                entity.getTimeInterval().endTime().format(TIME_FORMATTER),
                entity.getStatus().toString()
        );
    }

    public static List<AvailabilitySlotResource> toResourceListFromEntities(List<AvailabilitySlot> entities) {
        return entities.stream()
                .map(AvailabilitySlotResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
    }
}