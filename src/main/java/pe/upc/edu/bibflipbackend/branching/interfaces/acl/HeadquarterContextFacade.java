package pe.upc.edu.bibflipbackend.branching.interfaces.acl;

import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetHeadquarterByIdQuery;
import pe.upc.edu.bibflipbackend.branching.domain.services.HeadquarterQueryService;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service
public class HeadquarterContextFacade {
    private final HeadquarterQueryService headquarterQueryService;

    public HeadquarterContextFacade(HeadquarterQueryService headquarterQueryService) {
        this.headquarterQueryService = headquarterQueryService;
    }

    public Optional<LocalTime> getOpeningTime(Long headquarterId) {
        var query = new GetHeadquarterByIdQuery(headquarterId);
        var headquarter = headquarterQueryService.handle(query);
        if (headquarter.isEmpty()) return Optional.empty();
        return headquarter.map(hq -> hq.getOpeningTime() != null ? hq.getOpeningTime() : LocalTime.MIN);
    }

    public Optional<LocalTime> getClosingTime(Long headquarterId) {
        var query = new GetHeadquarterByIdQuery(headquarterId);
        var headquarter = headquarterQueryService.handle(query);
        if (headquarter.isEmpty()) return Optional.empty();
        return headquarter.map(hq -> hq.getClosingTime() != null ? hq.getClosingTime() : LocalTime.MAX);
    }

    public Optional<Integer> getIntervalMinutes(Long headquarterId) {
        var query = new GetHeadquarterByIdQuery(headquarterId);
        var headquarter = headquarterQueryService.handle(query);
        if (headquarter.isEmpty()) return Optional.empty();
        return headquarter.map(hq -> hq.getIntervalMinutes() != null ? hq.getIntervalMinutes() : 0);
    }

    public boolean existsHeadquarter(Long headquarterId) {
         var query = new GetHeadquarterByIdQuery(headquarterId);
         headquarterQueryService.handle(query);
         return headquarterQueryService.handle(query).isPresent();
    }
}
