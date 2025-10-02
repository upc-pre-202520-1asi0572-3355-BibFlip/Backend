package pe.upc.edu.bibflipbackend.branching.domain.services;

import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetAllHeadquarterSupervisorByIdHeadquarter;
import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetHeadquarterIdBySupervisorId;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.HeadquarterData;

import java.util.List;
import java.util.Optional;

public interface HeadquarterSupervisorQueryService {
    List<HeadquarterData> handle(GetAllHeadquarterSupervisorByIdHeadquarter query);
    Optional<HeadquarterData> handle(GetHeadquarterIdBySupervisorId headquarterData);
}
