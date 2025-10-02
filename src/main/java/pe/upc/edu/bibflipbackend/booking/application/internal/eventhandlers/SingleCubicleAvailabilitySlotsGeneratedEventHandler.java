package pe.upc.edu.bibflipbackend.booking.application.internal.eventhandlers;

import pe.upc.edu.bibflipbackend.booking.domain.model.commands.CreateCubicleScheduleCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.events.SingleCubicleAvailabilitySlotsGeneratedEvent;
import pe.upc.edu.bibflipbackend.booking.domain.services.CubicleCommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class SingleCubicleAvailabilitySlotsGeneratedEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleCubicleAvailabilitySlotsGeneratedEventHandler.class);

    private final CubicleCommandService cubicleCommandService;

    public SingleCubicleAvailabilitySlotsGeneratedEventHandler(CubicleCommandService cubicleCommandService) {
        this.cubicleCommandService = cubicleCommandService;
    }

    @EventListener(SingleCubicleAvailabilitySlotsGeneratedEvent.class)
    public void on(SingleCubicleAvailabilitySlotsGeneratedEvent event) {
        LOGGER.info("Processing SingleCubicleAvailabilitySlotsGeneratedEvent for cubicle ID: {}", event.getCubicleId());

        try {
            var createCubicleSchedulesCommand = new CreateCubicleScheduleCommand(event.getCubicleId());
            var cubicleOptional = cubicleCommandService.handle(createCubicleSchedulesCommand);

            if (cubicleOptional.isPresent()) {
                LOGGER.info("Successfully generated availability slots for cubicle ID: {}", event.getCubicleId());
            } else {
                LOGGER.warn("No cubiclereturned after processing cubicle ID: {}", event.getCubicleId());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing availability slots for cubicle ID: {}: {}",
                    event.getCubicleId(), e.getMessage(), e);
        }

    }
}
