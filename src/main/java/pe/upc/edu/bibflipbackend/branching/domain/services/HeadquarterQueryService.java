package pe.upc.edu.bibflipbackend.branching.domain.services;

import pe.upc.edu.bibflipbackend.branching.domain.model.aggregates.Headquarter;
import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetAllHeadquartersQuery;
import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetHeadquarterByIdQuery;

import java.util.List;
import java.util.Optional;

public interface HeadquarterQueryService {
    List<Headquarter> handle(GetAllHeadquartersQuery query);
    Optional<Headquarter> handle(GetHeadquarterByIdQuery query);
}
