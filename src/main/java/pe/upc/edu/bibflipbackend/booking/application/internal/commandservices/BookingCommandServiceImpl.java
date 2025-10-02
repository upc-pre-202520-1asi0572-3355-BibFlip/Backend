package pe.upc.edu.bibflipbackend.booking.application.internal.commandservices;

import pe.upc.edu.bibflipbackend.booking.application.internal.outboundedservices.acl.ExternalUserService;
import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Booking;
import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Cubicle;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.CreateBookingCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.DeleteBookingCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.entities.AvailabilitySlot;
import pe.upc.edu.bibflipbackend.booking.domain.model.entities.BookingSlot;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.ScheduleSlotStatus;
import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.UserId;
import pe.upc.edu.bibflipbackend.booking.domain.services.BookingCommandService;
import pe.upc.edu.bibflipbackend.booking.infrastructure.persistence.jpa.repositories.BookingRepository;
import pe.upc.edu.bibflipbackend.booking.infrastructure.persistence.jpa.repositories.CubicleRepository;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.InvalidValueException;
import pe.upc.edu.bibflipbackend.shared.application.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingCommandServiceImpl implements BookingCommandService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingCommandServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final CubicleRepository cubicleRepository;
    private final ExternalUserService externalUserService;

    public BookingCommandServiceImpl(BookingRepository bookingRepository,
                                     ExternalUserService externalUserService,
                                     CubicleRepository cubicleRepository) {
        this.bookingRepository = bookingRepository;
        this.externalUserService = externalUserService;
        this.cubicleRepository = cubicleRepository;
    }

    @Override
    @Transactional
    public Optional<Booking> handle(CreateBookingCommand command) {
        if (!externalUserService.existUserById(command.clientId())) {
            throw new IllegalArgumentException("User does not exist");
        }

        // Retrieve the cubicle with a pessimistic lock to avoid concurrent modifications
        Cubicle cubicle = cubicleRepository.findByIdWithSlotsForUpdate(command.cubicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cubicle not found with ID: " + command.cubicleId()));

        // Filter AvailabilitySlots for the specified date and matching the requested slot IDs
        List<AvailabilitySlot> slotsForDate = cubicle.getAvailabilitySlots().stream()
                .filter(slot -> slot.getDateOfSlot().equals(command.bookingDate())
                        && command.slotIds().contains(slot.getId()))
                .collect(Collectors.toList());

        // Verify that all requested slots have been found
        if (slotsForDate.size() != command.slotIds().size()) {
            throw new ResourceNotFoundException("Some requested availability slots were not found");
        }

        // Validate that each slot is in AVAILABLE state
        boolean allAvailable = slotsForDate.stream()
                .allMatch(slot -> slot.getStatus().equals(ScheduleSlotStatus.AVAILABLE));
        if (!allAvailable) {
            throw new InvalidValueException("One or more slots are not available for booking.");
        }

        // Sort the slots by startTime
        slotsForDate.sort((s1, s2) ->
                s1.getTimeInterval().startTime().compareTo(s2.getTimeInterval().startTime()));

        // Validate that the slots are consecutive
        for (int i = 1; i < slotsForDate.size(); i++) {
            LocalTime endPrev = slotsForDate.get(i - 1).getTimeInterval().endTime();
            LocalTime startCurrent = slotsForDate.get(i).getTimeInterval().startTime();
            if (!endPrev.equals(startCurrent)) {
                throw new InvalidValueException("The slots must be consecutive.");
            }
        }

        // Calculate the total duration in minutes
        LocalTime startTime = slotsForDate.get(0).getTimeInterval().startTime();
        LocalTime endTime = slotsForDate.get(slotsForDate.size() - 1).getTimeInterval().endTime();
        long totalMinutes = Duration.between(startTime, endTime).toMinutes();
        if (totalMinutes > 120) {
            throw new InvalidValueException("Cannot book more than 2 hours. Requested duration: " + totalMinutes + " minutes.");
        }
        LOGGER.info("Total booking duration: {} minutes", totalMinutes);

        // Create BookingSlots from each AvailabilitySlot and update its status to RESERVED
        Set<BookingSlot> bookingSlots = new HashSet<>();
        for (AvailabilitySlot slot : slotsForDate) {
            BookingSlot bookingSlot = new BookingSlot(slot.getTimeInterval());
            bookingSlots.add(bookingSlot);
            // Because of the pessimistic lock, no other transaction can update this slot concurrently.
            slot.updateStatus(ScheduleSlotStatus.RESERVED);
        }

        // Create the Booking entity
        Booking booking = new Booking(
                new UserId(command.clientId()),
                cubicle,
                command.bookingDate(),
                bookingSlots
        );

        // Persist the booking (changes in cubicle.availabilitySlots are cascaded)
        bookingRepository.save(booking);
        cubicleRepository.save(cubicle);

        LOGGER.info("Booking created successfully with ID: {}", booking.getId());
        return Optional.of(booking);
    }

    @Override
    @Transactional
    public void handle(DeleteBookingCommand command) {
        LOGGER.info("Processing request to delete booking with ID: {}", command.bookingId());

        // Find booking by ID or throw exception if it doesn't exist
        Booking booking = bookingRepository.findById(command.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + command.bookingId()));

        // Get cubicle with pessimistic lock to prevent concurrent modifications
        Cubicle cubicle = cubicleRepository.findByIdWithSlotsForUpdate(booking.getCubicleId().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cubicle not found"));

        // Identify availability slots to release
        List<AvailabilitySlot> slotsToUpdate = cubicle.getAvailabilitySlots().stream()
                .filter(slot -> slot.getDateOfSlot().equals(booking.getBookingDate()) &&
                        slot.getStatus() == ScheduleSlotStatus.RESERVED &&
                        booking.getBookingSlots().stream().anyMatch(bookingSlot ->
                                bookingSlot.getTimeInterval().equals(slot.getTimeInterval())))
                .toList();

        // Verify all slots associated with the booking were found
        if (slotsToUpdate.size() != booking.getBookingSlots().size()) {
            LOGGER.warn("Not all slots associated with booking ID: {} were found", booking.getId());
        }

        // Update slots status to AVAILABLE
        for (AvailabilitySlot slot : slotsToUpdate) {
            slot.updateStatus(ScheduleSlotStatus.AVAILABLE);
        }

        // Save cubicle with updated slots
        cubicleRepository.save(cubicle);

        // Delete booking
        bookingRepository.delete(booking);

        LOGGER.info("Booking successfully deleted with ID: {}", command.bookingId());
    }


}
