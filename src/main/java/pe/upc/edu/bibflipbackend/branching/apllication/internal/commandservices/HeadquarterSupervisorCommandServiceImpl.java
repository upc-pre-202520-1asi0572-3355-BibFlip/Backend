package pe.upc.edu.bibflipbackend.branching.apllication.internal.commandservices;

import pe.upc.edu.bibflipbackend.branching.apllication.internal.outboundedservice.acl.ExternalIamService;
import pe.upc.edu.bibflipbackend.branching.domain.model.commands.AddSupervisorToHeadquarterCommand;
import pe.upc.edu.bibflipbackend.branching.domain.model.commands.RemoveSupervisorFromHeadquarterCommand;
import pe.upc.edu.bibflipbackend.branching.domain.model.entities.HeadquarterSupervisor;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.HeadquarterData;
import pe.upc.edu.bibflipbackend.branching.domain.services.HeadquarterSupervisorCommandService;
import pe.upc.edu.bibflipbackend.branching.infrastructure.persistence.jpa.repositories.HeadquarterRepository;
import pe.upc.edu.bibflipbackend.branching.infrastructure.persistence.jpa.repositories.HeadquarterSupervisorRepository;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class HeadquarterSupervisorCommandServiceImpl implements HeadquarterSupervisorCommandService {

    private final HeadquarterRepository headquarterRepository;
    private final HeadquarterSupervisorRepository headquarterSupervisorRepository;
    private final ExternalIamService externalIamService;
    private static final Logger logger = LoggerFactory.getLogger(HeadquarterSupervisorCommandServiceImpl.class);

    public HeadquarterSupervisorCommandServiceImpl(
            HeadquarterRepository headquarterRepository,
            HeadquarterSupervisorRepository headquarterSupervisorRepository,
            ExternalIamService externalIamService) {
        this.headquarterRepository = headquarterRepository;
        this.headquarterSupervisorRepository = headquarterSupervisorRepository;
        this.externalIamService = externalIamService;
    }

    @Override
    @Transactional
    public Optional<HeadquarterData> handle(AddSupervisorToHeadquarterCommand command) {
        logger.info("Processing command to add supervisor with ID {} to headquarter {}",
                command.userId().userId(), command.headquarterId());

        // Validate headquarter exists
        var headquarterOptional = headquarterRepository.findById(command.headquarterId());
        if (headquarterOptional.isEmpty()) {
            logger.warn("Headquarter with ID {} not found", command.headquarterId());
            throw new ResourceNotFoundException("Headquarter " + command.headquarterId());
        }

        var headquarter = headquarterOptional.get();

        // Validate supervisor exists in IAM system
        if (externalIamService.existsUserById(command.userId().userId())) {
            logger.warn("Supervisor with ID {} not found", command.userId().userId());
            throw new ResourceNotFoundException("Supervisor " + command.userId().userId());
        }

        // Validar que el usuario tenga rol SUPERVISOR
        if (!externalIamService.isSupervisor(command.userId().userId())) {
            logger.warn("User with ID {} does not have SUPERVISOR role", command.userId().userId());
            throw new IllegalArgumentException("El usuario con ID " + command.userId().userId() +
                    " no tiene el rol SUPERVISOR requerido para ser asignado como supervisor");
        }

        // Check if relationship already exists
        var existingSupervisorOptional = headquarterSupervisorRepository.findByUserId(command.userId())
                .stream()
                .filter(hs -> hs.getHeadquarter().getId().equals(command.headquarterId()))
                .findFirst();

        if (existingSupervisorOptional.isPresent()) {
            logger.info("Supervisor is already assigned to this headquarter");
        } else {
            // Create new assignment
            var headquarterSupervisor = new HeadquarterSupervisor(headquarter, command.userId());
            headquarterSupervisorRepository.save(headquarterSupervisor);
            logger.info("Supervisor successfully assigned to headquarter");
        }

        String username = externalIamService.getUsernameById(command.userId().userId());
        return Optional.of(new HeadquarterData(command.userId().userId(), username, command.headquarterId()));
    }

    @Override
    @Transactional
    public Optional<HeadquarterData> handle(RemoveSupervisorFromHeadquarterCommand command) {
        logger.info("Procesando comando para eliminar supervisor {} de la sede {}",
                command.userId().userId(), command.headquarterId());

        // Validar que la sede existe
        var headquarterOptional = headquarterRepository.findById(command.headquarterId());
        if (headquarterOptional.isEmpty()) {
            logger.warn("Sede con ID {} no encontrada", command.headquarterId());
            throw new ResourceNotFoundException("Sede " + command.headquarterId());
        }

        // Validar que el usuario existe
        Long userId = command.userId().userId();
        if (externalIamService.existsUserById(userId)) {
            logger.warn("Usuario con ID {} no encontrado", userId);
            throw new ResourceNotFoundException("Usuario " + userId);
        }

        // Verificar que el usuario está asignado como supervisor en esta sede
        var existingSupervisor = headquarterSupervisorRepository.findByUserId(command.userId())
                .stream()
                .filter(hs -> hs.getHeadquarter().getId().equals(command.headquarterId()))
                .findFirst();

        if (existingSupervisor.isEmpty()) {
            logger.warn("El usuario {} no está asignado como supervisor en la sede {}", userId, command.headquarterId());
            throw new ResourceNotFoundException("El supervisor " + userId + " no está asignado a la sede " + command.headquarterId());
        }

        // Obtener username antes de eliminar
        String username = externalIamService.getUsernameById(userId);

        // Eliminar la asignación
        headquarterSupervisorRepository.deleteByHeadquarterIdAndUserId(command.headquarterId(), command.userId());
        logger.info("Supervisor eliminado correctamente de la sede");

        return Optional.of(new HeadquarterData(userId, username, command.headquarterId()));
    }
}