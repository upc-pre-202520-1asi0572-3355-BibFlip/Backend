package pe.upc.edu.bibflipbackend.booking.interfaces.rest;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Booking;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.DeleteBookingCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsByIdClientQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetBookingByIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.services.BookingCommandService;
import pe.upc.edu.bibflipbackend.booking.domain.services.BookingQueryService;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.BookingResource;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources.CreateBookingResource;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform.BookingResourceFromEntityAssembler;
import pe.upc.edu.bibflipbackend.booking.interfaces.rest.transform.CreateBookingCommandFromResourceAssembler;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import pe.upc.edu.bibflipbackend.shared.interfaces.rest.resources.SuccessMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            // In a real scenario, you might handle this scenario more specifically.
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
}
