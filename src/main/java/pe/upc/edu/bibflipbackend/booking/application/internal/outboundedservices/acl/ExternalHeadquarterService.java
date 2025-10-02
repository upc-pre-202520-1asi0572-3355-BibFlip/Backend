package pe.upc.edu.bibflipbackend.booking.application.internal.outboundedservices.acl;

import pe.upc.edu.bibflipbackend.branching.interfaces.acl.HeadquarterContextFacade;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service
public class ExternalHeadquarterService {

    private final HeadquarterContextFacade headquarterContextFacade;

    public ExternalHeadquarterService(HeadquarterContextFacade headquarterContextFacade) {
        this.headquarterContextFacade = headquarterContextFacade;
    }

    public Optional<LocalTime> getHeadquarterOpeningTime(Long headquarterId) {
        var openingTime = headquarterContextFacade.getOpeningTime(headquarterId);
        if (openingTime.isEmpty()) throw new ResourceNotFoundException("Headquarter not found with id: " + headquarterId);
        return openingTime;
    }

    public Optional<LocalTime> getHeadquarterClosingTime(Long headquarterId) {
        var closingTime = headquarterContextFacade.getClosingTime(headquarterId);
        if (closingTime.isEmpty()) throw new ResourceNotFoundException("Headquarter not found with id: " + headquarterId);
        return closingTime;
    }

    public Optional<Integer> getHeadquarterIntervalMinutes(Long headquarterId) {
        var intervalMinutes = headquarterContextFacade.getIntervalMinutes(headquarterId);
        if (intervalMinutes.isEmpty()) throw new ResourceNotFoundException("Headquarter not found with id: " + headquarterId);
        return intervalMinutes;
    }

    public boolean existsHeadquarter(Long headquarterId) {
        return headquarterContextFacade.existsHeadquarter(headquarterId);
    }
}
