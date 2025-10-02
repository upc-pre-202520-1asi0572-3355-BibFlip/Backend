package pe.upc.edu.bibflipbackend.booking.application.internal.queryservices;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Cubicle;
import pe.upc.edu.bibflipbackend.booking.domain.model.entities.AvailabilitySlot;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllCubicleByHeadquarterIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllCubiclesQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetCubicleByIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetCubicleScheduleByIdAndDateQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.HeadquarterId;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.CubicleStatus;
import pe.upc.edu.bibflipbackend.booking.domain.services.CubicleQueryService;
import pe.upc.edu.bibflipbackend.booking.infrastructure.persistence.jpa.repositories.CubicleRepository;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CubicleQueryServiceImpl implements CubicleQueryService {

    private final CubicleRepository cubicleRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(CubicleQueryServiceImpl.class);

    public CubicleQueryServiceImpl(CubicleRepository cubicleRepository) {
        this.cubicleRepository = cubicleRepository;
    }

    @Override
    public Optional<Cubicle> handle(GetCubicleByIdQuery query) {
        LOGGER.info("Searching for cubicle with ID: {}", query.id());
        return cubicleRepository.findById(query.id())
                .map(cubicle -> {
                    if (cubicle.getStatus() == CubicleStatus.DELETED) {
                        LOGGER.warn("Cubicle with ID {} is marked as DELETED", query.id());
                        throw new ResourceNotFoundException("No cubiclefound with ID: " + query.id());
                    }
                    LOGGER.info("Cubicle found with ID {}: {}", query.id(), cubicle);
                    return cubicle;
                })
                .or(() -> {
                    LOGGER.warn("No cubiclefound with ID: {}", query.id());
                    throw new ResourceNotFoundException("No cubiclefound with ID: " + query.id());
                });
    }

    @Override
    public Set<Cubicle> handle(GetAllCubiclesQuery query) {
        LOGGER.info("Starting query for all cubicles");
        var cubicles = cubicleRepository.findAll().stream()
                .filter(cubicle -> cubicle.getStatus() != CubicleStatus.DELETED)
                .collect(Collectors.toList());

        if (cubicles.isEmpty()) {
            LOGGER.warn("No cubicles found in the database");
            throw new ResourceNotFoundException("No cubicles found");
        }

        LOGGER.info("Query successful: {} cubicles found", cubicles.size());
        return new HashSet<>(cubicles);
    }

    @Override
    public List<AvailabilitySlot> handle(GetCubicleScheduleByIdAndDateQuery query) {
        LOGGER.info("Searching for availability slots for cubicle ID: {} on date: {}", query.cubicleId(), query.date());

        // Check if the cubicle exists
        if (!cubicleRepository.existsById(query.cubicleId())) {
            LOGGER.warn("No cubiclefound with ID: {}", query.cubicleId());
            throw new ResourceNotFoundException("No cubiclefound with ID: " + query.cubicleId());
        }

        // Get availability slots
        List<AvailabilitySlot> availabilitySlots = cubicleRepository.findAvailabilitySlotsByCubicleIdAndDate(query.cubicleId(), query.date());

        // Ordenar los slots por startTime antes de retornar
        availabilitySlots.sort(Comparator.comparing(slot -> slot.getTimeInterval().startTime()));

        if (availabilitySlots.isEmpty()) {
            LOGGER.info("No availability slots found for cubicle ID: {} on date: {}", query.cubicleId(), query.date());
        } else {
            LOGGER.info("Found {} availability slots for cubicle ID: {} on date: {}",
                    availabilitySlots.size(), query.cubicleId(), query.date());
        }

        return availabilitySlots;
    }

    @Override
    public List<Cubicle> handle(GetAllCubicleByHeadquarterIdQuery query) {
        LOGGER.info("Searching for cubicles associated with headquarter ID: {}", query.headquarterId());
        
        var headquarterId = new HeadquarterId(query.headquarterId());
        List<Cubicle> filteredCubicles = cubicleRepository.findByHeadquarterIdAndStatusNot(headquarterId, CubicleStatus.DELETED);

        if (filteredCubicles.isEmpty()) {
            LOGGER.warn("No cubicles found for headquarter with ID: {}", query.headquarterId());
            throw new ResourceNotFoundException("No cubicles found for headquarter with ID: " + query.headquarterId());
        }

        LOGGER.info("Found {} cubicles for headquarter with ID: {}", filteredCubicles.size(), query.headquarterId());
        return filteredCubicles;
    }

}