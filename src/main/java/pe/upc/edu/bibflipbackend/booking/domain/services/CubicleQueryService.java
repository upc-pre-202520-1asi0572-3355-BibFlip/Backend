package pe.upc.edu.bibflipbackend.booking.domain.services;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Cubicle;
import pe.upc.edu.bibflipbackend.booking.domain.model.entities.AvailabilitySlot;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllCubicleByHeadquarterIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllCubiclesQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetCubicleByIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetCubicleScheduleByIdAndDateQuery;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CubicleQueryService {
    Optional<Cubicle> handle(GetCubicleByIdQuery query);
    Set<Cubicle> handle(GetAllCubiclesQuery query);
    List<AvailabilitySlot> handle(GetCubicleScheduleByIdAndDateQuery query);
    List<Cubicle> handle(GetAllCubicleByHeadquarterIdQuery query);
}
