package pe.upc.edu.bibflipbackend.booking.application.internal.commandservices;

import pe.upc.edu.bibflipbackend.booking.application.internal.outboundedservices.acl.ExternalHeadquarterService;
import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Cubicle;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.*;
import pe.upc.edu.bibflipbackend.booking.domain.model.entities.AvailabilitySlot;
import pe.upc.edu.bibflipbackend.booking.domain.model.events.SingleCubicleAvailabilitySlotsGeneratedEvent;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.*;
import pe.upc.edu.bibflipbackend.booking.domain.services.CubicleCommandService;
import pe.upc.edu.bibflipbackend.booking.infrastructure.persistence.jpa.repositories.CubicleRepository;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceAlreadyException;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
public class CubicleCommandServiceImpl implements CubicleCommandService {
    private final CubicleRepository cubicleRepository;
    private final ExternalHeadquarterService externalHeadquarterService;
    private final ApplicationEventPublisher eventPublisher; // Nuevo
    private static final Logger LOGGER = LoggerFactory.getLogger(CubicleCommandServiceImpl.class);

    public CubicleCommandServiceImpl(CubicleRepository cubicleRepository, ExternalHeadquarterService externalHeadquarterService, ApplicationEventPublisher eventPublisher) {
        this.cubicleRepository = cubicleRepository;
        this.externalHeadquarterService = externalHeadquarterService;
        this.eventPublisher = eventPublisher; // Nuevo
    }

    @Override
    public Optional<Cubicle> handle(CreateCubicleCommand command) {
        LOGGER.info("Starting to process create cubicle command with headquarters ID: {} and cubicle number: {}", command.headquarterId(), command.cubicleNumber());
        var headquarterId = new HeadquarterId(command.headquarterId());

        if(!externalHeadquarterService.existsHeadquarter(command.headquarterId())) {
            throw new ResourceNotFoundException("Headquarter with ID: " + command.headquarterId() + " not found");
        }

        LOGGER.debug("Checking if cubicle with number: {} already exists in headquarters: {}", command.cubicleNumber(), command.headquarterId());
        if(cubicleRepository.existsByHeadquarterIdAndCubicleDetails_CubicleNumberAndStatusNot(headquarterId, command.cubicleNumber(), CubicleStatus.DELETED)) {
            LOGGER.warn("Cubicle with number: {} already exists in headquarters: {}", command.cubicleNumber(), command.headquarterId());
            throw new ResourceAlreadyException("Cubicle with number: " + command.cubicleNumber() + " already exists in headquarters: " + command.headquarterId());
        }

        LOGGER.info("Creating new cubicle with number: {} in headquarters: {}", command.cubicleNumber(), command.headquarterId());
        var cubicleDetails = new CubicleDetails(command.cubicleNumber(), command.seats());
        
        // Convertir string a CubicleZone enum
        var cubicleZone = CubicleZone.fromString(command.zone());
        // Crear tabla con zona
        var cubicle= new Cubicle(cubicleDetails, headquarterId, cubicleZone);

        try {
            LOGGER.debug("Saving cubicle to repository");
            var savedCubicle = cubicleRepository.save(cubicle);
            LOGGER.info("Cubicle created successfully with ID: {}", cubicle.getId());

            // Generar slots de disponibilidad automáticamente
            //cubicle.generateAvailabilitySlots();
            // Publicar evento manualmente después del guardado
            eventPublisher.publishEvent(
                    new SingleCubicleAvailabilitySlotsGeneratedEvent(this, savedCubicle.getId()));
            LOGGER.info("Generated availability slots for cubicle with ID: {}", savedCubicle.getId());

        } catch (Exception e) {
            LOGGER.error("Error while saving cubicle: {}", e.getMessage(), e);
            throw new RuntimeException("Error while saving cubicle: " + e.getMessage());
        }

        return Optional.of(cubicle);
    }

    @Override
    public Optional<Cubicle> handle(CreateCubicleScheduleCommand command) {
        LOGGER.info("Starting cubicle schedule creation for cubicle ID: {}", command.cubicleId());

        var cubicle= cubicleRepository.findById(command.cubicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cubicle with ID: " + command.cubicleId() + " not found"));

        var headquarterId = cubicle.getHeadquarterId();

        if(!cubicle.getAvailabilitySlots().isEmpty()) {
            LOGGER.warn("Cubicle with ID: {} already has availability slots", command.cubicleId());
            throw new RuntimeException("Cubicle with ID: " + command.cubicleId() + " already has availability slots");
        }

        LOGGER.debug("Checking if headquarters with ID: {} exists", headquarterId.headquarterId());
        if(!externalHeadquarterService.existsHeadquarter(headquarterId.headquarterId())) {
            LOGGER.error("Headquarters with ID: {} not found", headquarterId.headquarterId());
            throw new ResourceNotFoundException("Headquarter with ID: " + headquarterId.headquarterId() + " not found");
        }

        var headquarterOpeningTimeOpt = externalHeadquarterService.getHeadquarterOpeningTime(headquarterId.headquarterId());
        var headquarterClosingTimeOpt = externalHeadquarterService.getHeadquarterClosingTime(headquarterId.headquarterId());

        if(headquarterOpeningTimeOpt.get() == LocalTime.MIN && headquarterClosingTimeOpt.get() == LocalTime.MAX) {
            LOGGER.error("Error while retrieving headquarters schedules");
            throw new RuntimeException("Error while retrieving opening and closing times for headquarters");
        }

        var intervalMinutesOpt = externalHeadquarterService.getHeadquarterIntervalMinutes(headquarterId.headquarterId());

        if(intervalMinutesOpt.isEmpty() || intervalMinutesOpt.get() == 0) {
            LOGGER.error("Error while retrieving interval minutes for headquarters");
            throw new RuntimeException("Error while retrieving interval minutes for headquarters");
        }

        LocalTime openingTime = headquarterOpeningTimeOpt.get();
        LocalTime closingTime = headquarterClosingTimeOpt.get();
        int intervalMinutes = intervalMinutesOpt.get();

        LOGGER.info("Generating slots from {} to {} with {} minutes intervals",
                openingTime, closingTime, intervalMinutes);

        // Obtener fecha actual
        LocalDate today = LocalDate.now();

        // Calcular el próximo domingo
        LocalDate nextSunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Generar slots para cada día hasta el domingo
        for (LocalDate currentDate = today; !currentDate.isAfter(nextSunday); currentDate = currentDate.plusDays(1)) {
            LOGGER.debug("Generating slots for day: {}", currentDate);

            // Generar slots para este día
            LocalTime currentTime = openingTime;
            while (currentTime.isBefore(closingTime)) {
                LocalTime endTime = currentTime.plusMinutes(intervalMinutes);

                if (endTime.isAfter(closingTime)) {
                    endTime = closingTime;
                }

                // Crear TimeSlot
                TimeSlot timeSlot = new TimeSlot(currentTime, endTime);

                // Crear AvailabilitySlot
                AvailabilitySlot slot = new AvailabilitySlot(currentDate, timeSlot);

                // Añadir el slot a la mesa
                cubicle.getAvailabilitySlots().add(slot);

                // Avanzar al siguiente intervalo
                currentTime = endTime;
            }
        }

        LOGGER.info("Created {} availability slots for the cubicle", cubicle.getAvailabilitySlots().size());

        // Guardar la mesa con sus nuevos slots
        cubicleRepository.save(cubicle);

        return Optional.of(cubicle);
    }

    @Override
    public void handle(DeleteCubicleCommand command) {
        LOGGER.info("Processing delete cubicle command for cubicle ID: {}", command.cubicleId());

        var cubicle= cubicleRepository.findById(command.cubicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cubicle with ID: " + command.cubicleId() + " not found"));

        LOGGER.debug("Marking cubicle with ID: {} as DELETED", command.cubicleId());
        cubicle.setStatus(CubicleStatus.DELETED);
        cubicleRepository.save(cubicle);
        LOGGER.info("Cubicle with ID: {} marked as DELETED successfully", command.cubicleId());
    }

    @Override
    public Optional<Cubicle> handle(UpdateCubicleStatusCommand command) {
        LOGGER.info("Processing update status command for cubicle ID: {} to status: {}",
                command.cubicleId(), command.status());

        var cubicle = cubicleRepository.findById(command.cubicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cubicle with ID: " + command.cubicleId() + " not found"));

        LOGGER.debug("Current status: {}, New status: {}", cubicle.getStatus(), command.status());

        // Update status
        cubicle.setStatus(command.status());

        try {
            var savedCubicle = cubicleRepository.save(cubicle);
            LOGGER.info("Cubicle status updated successfully for ID: {}", command.cubicleId());
            return Optional.of(savedCubicle);
        } catch (Exception e) {
            LOGGER.error("Error while updating cubicle status: {}", e.getMessage(), e);
            throw new RuntimeException("Error while updating cubicle status: " + e.getMessage());
        }
    }

    @Override
    public Optional<AvailabilitySlot> handle(UpdateAvailabilitySlotStatusCommand command) {
        LOGGER.info("Processing update availability slot status for cubicle ID: {} on date: {} at time: {}",
                command.cubicleId(), command.date(), command.currentTime());

        var cubicle = cubicleRepository.findById(command.cubicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cubicle with ID: " + command.cubicleId() + " not found"));

        // En lugar de buscar por hora exacta, buscamos el slot donde la hora actual está dentro del intervalo
        Optional<AvailabilitySlot> matchingSlot = cubicle.getAvailabilitySlots().stream()
                .filter(slot -> slot.getDateOfSlot().equals(command.date()))
                .filter(slot -> isTimeWithinSlot(command.currentTime(), slot))
                .findFirst();

        if (matchingSlot.isEmpty()) {
            LOGGER.warn("No availability slot found for cubicle ID: {} at time: {} on date: {}",
                    command.cubicleId(), command.currentTime(), command.date());

            // Log adicional para debugging
            LOGGER.debug("Available slots for cubicle {} on {}: {}",
                    command.cubicleId(),
                    command.date(),
                    cubicle.getAvailabilitySlots().stream()
                            .filter(s -> s.getDateOfSlot().equals(command.date()))
                            .map(s -> s.getTimeInterval().startTime() + "-" + s.getTimeInterval().endTime())
                            .toList()
            );

            return Optional.empty();
        }

        AvailabilitySlot slot = matchingSlot.get();
        ScheduleSlotStatus oldStatus = slot.getStatus();

        // Update status
        slot.updateStatus(command.status());

        try {
            cubicleRepository.save(cubicle);
            LOGGER.info("Availability slot ID: {} status updated from {} to {}",
                    slot.getId(), oldStatus, command.status());
            return Optional.of(slot);
        } catch (Exception e) {
            LOGGER.error("Error while updating availability slot status: {}", e.getMessage(), e);
            throw new RuntimeException("Error while updating availability slot status: " + e.getMessage());
        }
    }

    /**
     * MÉTODO CORREGIDO: Verifica si una hora está DENTRO del intervalo del slot
     * Antes: Verificaba si currentTime == startTime (muy estricto)
     * Ahora: Verifica si startTime <= currentTime < endTime
     */
    private boolean isTimeWithinSlot(LocalTime currentTime, AvailabilitySlot slot) {
        LocalTime start = slot.getTimeInterval().startTime();
        LocalTime end = slot.getTimeInterval().endTime();

        // currentTime debe estar entre start y end time
        // Ejemplo: slot 10:00-11:00, currentTime 10:30 → TRUE
        // Ejemplo: slot 10:00-11:00, currentTime 11:00 → FALSE (pertenece al siguiente slot)
        return !currentTime.isBefore(start) && currentTime.isBefore(end);
    }
}