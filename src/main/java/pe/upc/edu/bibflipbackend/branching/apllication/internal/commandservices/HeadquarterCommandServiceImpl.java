package pe.upc.edu.bibflipbackend.branching.apllication.internal.commandservices;

import pe.upc.edu.bibflipbackend.branching.domain.model.aggregates.Headquarter;
import pe.upc.edu.bibflipbackend.branching.domain.model.commands.CreateHeadquarterCommand;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.Coordinates;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.NameHeadquarter;
import pe.upc.edu.bibflipbackend.branching.domain.services.HeadquarterCommandService;
import pe.upc.edu.bibflipbackend.branching.infrastructure.persistence.jpa.repositories.HeadquarterRepository;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceAlreadyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HeadquarterCommandServiceImpl implements HeadquarterCommandService {

    private final HeadquarterRepository headquarterRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(HeadquarterCommandServiceImpl.class);

    public HeadquarterCommandServiceImpl(HeadquarterRepository headquarterRepository) {
        this.headquarterRepository = headquarterRepository;
    }

    @Override
    public Optional<Headquarter> handle(CreateHeadquarterCommand command) {
        LOGGER.info("Starting to process create headquarter command with name: {}", command.name());

        LOGGER.debug("Checking if headquarter with name: {} already exists", command.name());
        if(headquarterRepository.existsByName(new NameHeadquarter(command.name()))) {
            LOGGER.warn("Headquarter with name: {} already exists", command.name());
            throw new ResourceAlreadyException("Headquarter with name: " + command.name() + " already exists");
        }

        LOGGER.debug("Checking if headquarter with coordinates: ({}, {}) already exists",
                command.latitude(), command.longitude());
        if(headquarterRepository.existsByCoordinates(new Coordinates(command.latitude(), command.longitude()))) {
            LOGGER.warn("Headquarter with coordinates: ({}, {}) already exists",
                    command.latitude(), command.longitude());
            throw new ResourceAlreadyException("Headquarter with coordinates: (" + command.latitude() + ", " + command.longitude() + ") already exists");
        }

        LOGGER.info("Creating new headquarter with name: {}", command.name());
        var headquarter = new Headquarter(command);

        try {
            LOGGER.debug("Saving headquarter to repository");
            headquarterRepository.save(headquarter);
            LOGGER.info("Headquarter created successfully with ID: {}", headquarter.getId());
        } catch (Exception e) {
            LOGGER.error("Error while saving headquarter: {}", e.getMessage(), e);
            throw new RuntimeException("Error while saving headquarter: " + e.getMessage());
        }

        return Optional.of(headquarter);
    }
}