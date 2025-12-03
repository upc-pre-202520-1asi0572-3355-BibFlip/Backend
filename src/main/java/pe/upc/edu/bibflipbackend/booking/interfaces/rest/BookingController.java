package pe.upc.edu.bibflipbackend.booking.interfaces.rest;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Booking;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.DeleteBookingCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsByIdClientQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetBookingByIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetBookingByCubicleAndTimeQuery;
import pe.upc.edu.bibflipbackend.booking.domain.services.BookingCommandService;
import pe.upc.edu.bibflipbackend.booking.domain.services.BookingQueryService;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.BookingResource;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.CreateBookingResource;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform.BookingResourceFromEntityAssembler;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform.CreateBookingCommandFromResourceAssembler;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import pe.upc.edu.bibflipbackend.shared.interfaces.rest.resources.SuccessMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Booking", description = "Booking Management Endpoints")
public class BookingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);
    private final BookingCommandService bookingCommandService;
    private final BookingQueryService bookingQueryService;

    public BookingController(BookingCommandService bookingCommandService, BookingQueryService bookingQueryService) {
        this.bookingCommandService = bookingCommandService;
        this.bookingQueryService = bookingQueryService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingResource> createBooking(@RequestBody CreateBookingResource createBookingResource) {
        LOGGER.info("Received create booking request for cubicle ID: {} and booking date: {}",
                createBookingResource.cubicleId(), createBookingResource.bookingDate());

        var command = CreateBookingCommandFromResourceAssembler.toCommandFromResource(createBookingResource);
        Optional<Booking> bookingOpt = bookingCommandService.handle(command);

        if (bookingOpt.isEmpty()) {
            LOGGER.error("Booking could not be created for cubicle ID: {}", createBookingResource.cubicleId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        BookingResource bookingResource = BookingResourceFromEntityAssembler.toResourceFromEntity(bookingOpt.get());
        LOGGER.info("Booking created successfully with ID: {}", bookingResource.id());
        return new ResponseEntity<>(bookingResource, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingResource> getBookingById(@PathVariable Long id) {
        LOGGER.info("Received request to retrieve booking with ID: {}", id);

        var query = new GetBookingByIdQuery(id);
        Optional<Booking> bookingOpt = bookingQueryService.handle(query);

        if (bookingOpt.isEmpty()) {
            LOGGER.error("Booking not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        BookingResource bookingResource = BookingResourceFromEntityAssembler.toResourceFromEntity(bookingOpt.get());
        LOGGER.info("Booking retrieved successfully with ID: {}", id);
        return ResponseEntity.ok(bookingResource);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookingResource>> getAllBookings() {
        LOGGER.info("Received request to retrieve all bookings");

        List<Booking> bookings = bookingQueryService.handle(new GetAllBookingsQuery());
        List<BookingResource> bookingResources = bookings.stream()
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        LOGGER.info("Retrieved {} bookings", bookingResources.size());
        return ResponseEntity.ok(bookingResources);
    }

    @GetMapping(value = "/client/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookingResource>> getAllBookingsByClientId(@PathVariable Long clientId) {
        LOGGER.info("Received request to retrieve all bookings for client ID: {}", clientId);

        var query = new GetAllBookingsByIdClientQuery(clientId);
        List<Booking> bookings = bookingQueryService.handle(query);
        List<BookingResource> bookingResources = bookings.stream()
                .map(BookingResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        LOGGER.info("Retrieved {} bookings for client ID: {}", bookingResources.size(), clientId);
        return ResponseEntity.ok(bookingResources);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<SuccessMessage> deleteBooking(@PathVariable Long id) {
        LOGGER.info("Received request to delete booking with ID: {}", id);

        try {
            var command = new DeleteBookingCommand(id);
            bookingCommandService.handle(command);

            LOGGER.info("Booking with ID: {} successfully deleted", id);
            return ResponseEntity.ok(new SuccessMessage(HttpStatus.OK.value(),
                    "Booking with ID: " + id + " successfully deleted"));
        } catch (ResourceNotFoundException e) {
            LOGGER.error("Error deleting booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new SuccessMessage(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    /**
     * NUEVO ENDPOINT: Cancelar booking activo por cubículo y hora
     * Usado por el IoT Edge API para liberar automáticamente un cubículo
     * cuando el sensor detecta que está disponible.
     */
    @DeleteMapping(value = "/cubicle/{cubicleId}/current")
    @Operation(
            summary = "Cancel current active booking for a cubicle",
            description = "Cancels the booking that is currently active for the specified cubicle at the given time. " +
                    "If no date/time is provided, uses current date/time. " +
                    "Used by IoT sensors to auto-release cubicles when they become available."
    )
    public ResponseEntity<SuccessMessage> cancelCurrentBooking(
            @PathVariable Long cubicleId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String time) {

        LOGGER.info("IoT request to cancel current booking for cubicle ID: {} (date: {}, time: {})",
                cubicleId, date, time);

        try {
            // Usar fecha/hora actual si no se proporcionan (zona horaria de Lima, UTC-5)
            LocalDate bookingDate = (date != null)
                    ? LocalDate.parse(date)
                    : LocalDate.now();

            LocalTime bookingTime = (time != null)
                    ? LocalTime.parse(time)
                    : LocalTime.now();

            LOGGER.debug("Searching for booking at cubicle {} on {} at {}",
                    cubicleId, bookingDate, bookingTime);

            // Buscar booking activo en ese momento
            var query = new GetBookingByCubicleAndTimeQuery(cubicleId, bookingDate, bookingTime);
            Optional<Booking> bookingOpt = bookingQueryService.handle(query);

            if (bookingOpt.isEmpty()) {
                LOGGER.info("No active booking found for cubicle {} at {} on {}",
                        cubicleId, bookingTime, bookingDate);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new SuccessMessage(HttpStatus.NOT_FOUND.value(),
                                "No active booking found for this cubicle at current time"));
            }

            // Eliminar el booking encontrado
            Booking booking = bookingOpt.get();
            LOGGER.info("Found active booking with ID: {} for cubicle {}. Proceeding to cancel.",
                    booking.getId(), cubicleId);

            var deleteCommand = new DeleteBookingCommand(booking.getId());
            bookingCommandService.handle(deleteCommand);

            LOGGER.info("✓ Booking {} successfully cancelled by IoT for cubicle {}. Cubicle is now available.",
                    booking.getId(), cubicleId);

            return ResponseEntity.ok(new SuccessMessage(HttpStatus.OK.value(),
                    "Current booking (ID: " + booking.getId() + ") cancelled successfully. Cubicle is now available."));

        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid parameters for cubicle {}: {}", cubicleId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SuccessMessage(HttpStatus.BAD_REQUEST.value(),
                            "Invalid parameters: " + e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unexpected error cancelling booking for cubicle {}: {}", cubicleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SuccessMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Error cancelling booking: " + e.getMessage()));
        }
    }
}