package pe.upc.edu.bibflipbackend.booking.application.internal.queryservices;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Booking;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsByIdClientQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetBookingByIdQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetBookingByCubicleAndTimeQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.UserId;
import pe.upc.edu.bibflipbackend.booking.domain.services.BookingQueryService;
import pe.upc.edu.bibflipbackend.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
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

    @Override
    public Optional<Booking> handle(GetBookingByCubicleAndTimeQuery query) {
        LOGGER.info("Searching for active booking - Cubicle: {}, Date: {}, Time: {}",
                query.cubicleId(), query.date(), query.time());

        // Buscar todos los bookings del cubículo en la fecha especificada
        List<Booking> bookingsForDate = bookingRepository.findAll().stream()
                .filter(booking ->
                        booking.getCubicleId().getId().equals(query.cubicleId()) &&
                                booking.getBookingDate().equals(query.date())
                )
                .toList();

        LOGGER.debug("Found {} bookings for cubicle {} on date {}",
                bookingsForDate.size(), query.cubicleId(), query.date());

        // Buscar el booking que contiene el tiempo especificado en alguno de sus slots
        Optional<Booking> activeBooking = bookingsForDate.stream()
                .filter(booking -> {
                    // El tiempo debe estar dentro del intervalo [startTime, endTime)

                    return booking.getBookingSlots().stream()
                            .anyMatch(slot -> {
                                LocalTime slotStart = slot.getTimeInterval().startTime();
                                LocalTime slotEnd = slot.getTimeInterval().endTime();

                                // El tiempo debe estar dentro del intervalo [startTime, endTime)
                                boolean isWithinSlot = !query.time().isBefore(slotStart) &&
                                        query.time().isBefore(slotEnd);

                                if (isWithinSlot) {
                                    LOGGER.debug("Found matching slot: {} - {}", slotStart, slotEnd);
                                }

                                return isWithinSlot;
                            });
                })
                .findFirst();

        if (activeBooking.isPresent()) {
            LOGGER.info("Active booking found with ID: {} for cubicle {} at time {}",
                    activeBooking.get().getId(), query.cubicleId(), query.time());
        } else {
            LOGGER.info("No active booking found for cubicle {} at time {} on date {}",
                    query.cubicleId(), query.time(), query.date());
        }

        return activeBooking;
    }
}