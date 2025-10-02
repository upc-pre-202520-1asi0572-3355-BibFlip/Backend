package pe.upc.edu.bibflipbackend.booking.application.internal.queryservices;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Booking;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsByIdClientQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetBookingByIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.UserId;
import pe.upc.edu.bibflipbackend.booking.domain.services.BookingQueryService;
import pe.upc.edu.bibflipbackend.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingQueryServiceImpl implements BookingQueryService {
    private final BookingRepository bookingRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingQueryServiceImpl.class);

    public BookingQueryServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Optional<Booking> handle(GetBookingByIdQuery query) {
        LOGGER.info("Searching for booking with ID: {}", query.bookingId());
        return bookingRepository.findById(query.bookingId())
                .map(
                        booking -> {
                            LOGGER.info("Booking found: {}", booking);
                            return booking;
                        }
                ).or(
                        () -> {
                            LOGGER.warn("Booking with ID {} not found", query.bookingId());
                            return Optional.empty();
                        }
                );

    }

    @Override
    public List<Booking> handle(GetAllBookingsQuery query) {
        LOGGER.info("Fetching all bookings");
        List<Booking> bookings = bookingRepository.findAll();
        if(bookings.isEmpty()) {
            LOGGER.warn("No bookings found");
            throw new ResourceNotFoundException("No bookings found");
        }
        LOGGER.info("Found {} bookings", bookings.size());

        return bookings;
    }

    @Override
    public List<Booking> handle(GetAllBookingsByIdClientQuery query) {
        LOGGER.info("Fetching all bookings for client ID: {}", query.clientId());
        List<Booking> bookings = bookingRepository.findAllByUserId(new UserId(query.clientId()));
        if(bookings.isEmpty()) {
            LOGGER.warn("No bookings found for client ID: {}", query.clientId());
            throw new ResourceNotFoundException("No bookings found for client ID: " + query.clientId());
        }
        LOGGER.info("Found {} bookings for client ID: {}", bookings.size(), query.clientId());

        return bookings;
    }
}
