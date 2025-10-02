package pe.upc.edu.bibflipbackend.branching.apllication.internal.queryservices;

import pe.upc.edu.bibflipbackend.branching.domain.model.aggregates.Headquarter;
import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetAllHeadquartersQuery;
import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetHeadquarterByIdQuery;
import pe.upc.edu.bibflipbackend.branching.domain.services.HeadquarterQueryService;
import pe.upc.edu.bibflipbackend.branching.infrastructure.persistence.jpa.repositories.HeadquarterRepository;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HeadquarterQueryServiceImpl implements HeadquarterQueryService {

    private final HeadquarterRepository headquarterRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(HeadquarterQueryServiceImpl.class.getName());

    public HeadquarterQueryServiceImpl(HeadquarterRepository headquarterRepository) {
        this.headquarterRepository = headquarterRepository;
    }

    @Override
    public List<Headquarter> handle(GetAllHeadquartersQuery query) {
        LOGGER.info("Starting query for all headquarters");
        return Optional.of(headquarterRepository.findAll())
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    LOGGER.info("Query successful: {} headquarters found", list.size());
                    return list;
                })
                .orElseThrow(() -> {
                    LOGGER.warn("No headquarters found in the database");
                    return new ResourceNotFoundException("No headquarters found in the database");
                });
    }

    @Override
    public Optional<Headquarter> handle(GetHeadquarterByIdQuery query) {
        LOGGER.info("Searching for headquarter with ID: {}", query.id());
        return headquarterRepository.findById(query.id())
                .map(headquarter -> {
                    LOGGER.info("Headquarter found with ID {}: {}", query.id(), headquarter);
                    return headquarter;
                })
                .or(() -> {
                    LOGGER.warn("No headquarter found with ID: {}", query.id());
                    throw new ResourceNotFoundException("No headquarter found with ID: " + query.id());
                });
    }
}