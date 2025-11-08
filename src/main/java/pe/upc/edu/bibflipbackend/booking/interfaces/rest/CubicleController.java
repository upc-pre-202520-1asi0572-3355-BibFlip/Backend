package pe.upc.edu.bibflipbackend.booking.interfaces.rest;

import pe.upc.edu.bibflipbackend.booking.domain.model.commands.DeleteCubicleCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.UpdateAvailabilitySlotStatusCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.UpdateCubicleStatusCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllCubicleByHeadquarterIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllCubiclesQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetCubicleByIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetCubicleScheduleByIdAndDateQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.CubicleStatus;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.ScheduleSlotStatus;
import pe.upc.edu.bibflipbackend.booking.domain.services.CubicleCommandService;
import pe.upc.edu.bibflipbackend.booking.domain.services.CubicleQueryService;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.*;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform.AvailabilitySlotResourceFromEntityAssembler;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform.CreateCubicleCommandFromResourceAssembler;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform.CubicleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/cubicles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Cubicle", description = "Cubicle Management Endpoints")
public class CubicleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CubicleController.class);
    private final CubicleCommandService cubicleCommandService;
    private final CubicleQueryService cubicleQueryService;

    public CubicleController(CubicleCommandService cubicleCommandService, CubicleQueryService cubicleQueryService) {
        this.cubicleCommandService = cubicleCommandService;
        this.cubicleQueryService = cubicleQueryService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CubicleResource> createCubicle(@RequestBody CreateCubicleResource createCubicleResource){
        var createCubicleCommand = CreateCubicleCommandFromResourceAssembler.toCommandFromResource(createCubicleResource);
        var cubicle= cubicleCommandService.handle(createCubicleCommand);
        var cubicleResource = CubicleResourceFromEntityAssembler.toResourceFromEntity(cubicle.get());
        return new ResponseEntity<>(cubicleResource, HttpStatus.CREATED);
    }

    @GetMapping(value = "{cubicleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CubicleResource> getCubicleById(@PathVariable Long cubicleId) {
        var getCubicleByIdQuery = new GetCubicleByIdQuery(cubicleId);
        var cubicle= cubicleQueryService.handle(getCubicleByIdQuery);
        var cubicleResource = CubicleResourceFromEntityAssembler.toResourceFromEntity(cubicle.get());
        return new ResponseEntity<>(cubicleResource, HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CubicleResource>> getAllCubicles() {
        var getAllCubiclesQuery = new GetAllCubiclesQuery();
        var cubicles = cubicleQueryService.handle(getAllCubiclesQuery);
        var headquarterResources = cubicles.stream()
                .map(CubicleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return new ResponseEntity<>(headquarterResources, HttpStatus.OK);
    }

    @GetMapping(value = "{cubicleId}/schedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AvailabilitySlotResource>> getCubicleSchedule(
            @PathVariable Long cubicleId,
            @RequestParam LocalDate date) {

        var getCubicleScheduleQuery = new GetCubicleScheduleByIdAndDateQuery(cubicleId, date);
        var availabilitySlots = cubicleQueryService.handle(getCubicleScheduleQuery);

        var availabilitySlotResources = AvailabilitySlotResourceFromEntityAssembler
                .toResourceListFromEntities(availabilitySlots);

        return new ResponseEntity<>(availabilitySlotResources, HttpStatus.OK);
    }

    @GetMapping(value = "/headquarter/{headquarterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CubicleResource>> getCubiclesByHeadquarterId(@PathVariable Long headquarterId) {
        LOGGER.info("Received request to get cubicles for headquarter with ID: {}", headquarterId);

        var getAllCubicleByHeadquarterIdQuery = new GetAllCubicleByHeadquarterIdQuery(headquarterId);
        var cubicles = cubicleQueryService.handle(getAllCubicleByHeadquarterIdQuery);

        var cubicleResources = cubicles.stream()
                .map(CubicleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        LOGGER.info("Found {} cubicles for headquarter ID: {}", cubicles.size(), headquarterId);
        return new ResponseEntity<>(cubicleResources, HttpStatus.OK);
    }

    @DeleteMapping(value = "{cubicleId}")
    public ResponseEntity<Void> deleteCubicle(@PathVariable Long cubicleId) {
        LOGGER.info("Received request to delete cubicle with ID: {}", cubicleId);

        var deleteCubicleCommand = new DeleteCubicleCommand(cubicleId);
        cubicleCommandService.handle(deleteCubicleCommand);

        LOGGER.info("Cubicle with ID: {} deleted successfully", cubicleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(value = "/{cubicleId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CubicleResource> updateCubicleStatus(
            @PathVariable Long cubicleId,
            @RequestBody UpdateCubicleStatusResource resource) {

        LOGGER.info("Received status update request for cubicle ID: {} to status: {}",
                cubicleId, resource.status());

        var command = new UpdateCubicleStatusCommand(cubicleId,
                CubicleStatus.valueOf(resource.status()));
        var cubicle = cubicleCommandService.handle(command);

        if (cubicle.isEmpty()) {
            LOGGER.warn("Cubicle with ID: {} not found", cubicleId);
            throw new ResourceNotFoundException("Cubicle with ID: " + cubicleId + " not found");
        }

        var cubicleResource = CubicleResourceFromEntityAssembler.toResourceFromEntity(cubicle.get());
        LOGGER.info("Cubicle status updated successfully for ID: {}", cubicleId);

        return new ResponseEntity<>(cubicleResource, HttpStatus.OK);
    }

    @PatchMapping(value = "/{cubicleId}/availability-slot/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AvailabilitySlotResource> updateAvailabilitySlotStatus(
            @PathVariable Long cubicleId,
            @RequestBody UpdateAvailabilitySlotStatusResource resource) {

        LOGGER.info("Received availability slot status update request for cubicle ID: {} to status: {}",
                cubicleId, resource.status());

        var command = new UpdateAvailabilitySlotStatusCommand(
                cubicleId,
                LocalDate.now(),
                LocalTime.now(),
                ScheduleSlotStatus.valueOf(resource.status())
        );

        var slotOpt = cubicleCommandService.handle(command);

        if (slotOpt.isEmpty()) {
            LOGGER.warn("No availability slot found for cubicle ID: {} at current time", cubicleId);
            throw new ResourceNotFoundException(
                    "No availability slot found for cubicle ID: " + cubicleId + " at current time");
        }

        var slot = slotOpt.get();
        var slotResource = new AvailabilitySlotResource(
                slot.getId(),
                slot.getDateOfSlot(),
                slot.getTimeInterval().startTime().toString(),
                slot.getTimeInterval().endTime().toString(),
                slot.getStatus().toString()
        );

        LOGGER.info("Availability slot status updated successfully for cubicle ID: {}", cubicleId);

        return new ResponseEntity<>(slotResource, HttpStatus.OK);
    }
}
