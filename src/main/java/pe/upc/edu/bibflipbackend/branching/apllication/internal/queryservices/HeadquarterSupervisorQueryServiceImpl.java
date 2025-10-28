package pe.upc.edu.bibflipbackend.branching.apllication.internal.queryservices;

import pe.upc.edu.bibflipbackend.branching.apllication.internal.outboundedservice.acl.ExternalIamService;
import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetAllHeadquarterSupervisorByIdHeadquarter;
import pe.upc.edu.bibflipbackend.branching.domain.model.queries.GetHeadquarterIdBySupervisorId;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.HeadquarterData;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.UserId;
import pe.upc.edu.bibflipbackend.branching.domain.services.HeadquarterSupervisorQueryService;
import pe.upc.edu.bibflipbackend.branching.infrastructure.persistence.jpa.repositories.HeadquarterRepository;
import pe.upc.edu.bibflipbackend.branching.infrastructure.persistence.jpa.repositories.HeadquarterSupervisorRepository;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HeadquarterSupervisorQueryServiceImpl implements HeadquarterSupervisorQueryService {

    private final HeadquarterRepository headquarterRepository;
    private final HeadquarterSupervisorRepository headquarterSupervisorRepository;
    private final ExternalIamService externalIamService;
    private static final Logger logger = LoggerFactory.getLogger(HeadquarterSupervisorQueryServiceImpl.class);

    public HeadquarterSupervisorQueryServiceImpl(
            HeadquarterRepository headquarterRepository,
            HeadquarterSupervisorRepository headquarterSupervisorRepository,
            ExternalIamService externalIamService) {
        this.headquarterRepository = headquarterRepository;
        this.headquarterSupervisorRepository = headquarterSupervisorRepository;
        this.externalIamService = externalIamService;
    }

    @Override
    public List<HeadquarterData> handle(GetAllHeadquarterSupervisorByIdHeadquarter query) {
        logger.info("Procesando consulta para obtener supervisores de la sede con ID {}", query.headquarterId());

        boolean headquarterExists = headquarterRepository.existsById(query.headquarterId());
        if (!headquarterExists) {
            logger.warn("Sede con ID {} no encontrada", query.headquarterId());
            throw new ResourceNotFoundException("Sede " + query.headquarterId());
        }

        var supervisors = headquarterSupervisorRepository.findByHeadquarterId(query.headquarterId());
        logger.info("Encontrados {} supervisores para la sede con ID {}", supervisors.size(), query.headquarterId());

        return supervisors.stream()
                .map(supervisor -> {
                    Long userId = supervisor.getUserId().userId();
                    String username = externalIamService.getUsernameById(userId);
                    return new HeadquarterData(userId, username, query.headquarterId());
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<HeadquarterData> handle(GetHeadquarterIdBySupervisorId query) {
        logger.info("Procesando consulta para obtener sede del supervisor con ID {}", query.supervisorId());

        // Validar que el supervisor existe
        Long supervisorId = query.supervisorId();
        if (externalIamService.existsUserById(supervisorId)) {
            logger.warn("Supervisor con ID {} no encontrado", supervisorId);
            throw new ResourceNotFoundException("Supervisor " + supervisorId);
        }

        // Buscar todas las asignaciones del supervisor
        var assignments = headquarterSupervisorRepository.findByUserId(new UserId(supervisorId));

        if (assignments.isEmpty()) {
            logger.warn("El supervisor con ID {} no está asignado a ninguna sede", supervisorId);
            throw new ResourceNotFoundException("El usuario con ID " + supervisorId + " no está asignado a ninguna sede");
        }

        // Si hay múltiples asignaciones, tomamos la primera
        // En un caso real, podría necesitarse una lógica más específica
        var assignment = assignments.get(0);
        var headquarterId = assignment.getHeadquarter().getId();
        String username = externalIamService.getUsernameById(supervisorId);

        logger.info("Encontrada sede con ID {} para el supervisor {}", headquarterId, supervisorId);

        return Optional.of(new HeadquarterData(supervisorId, username, headquarterId));
    }
}