package pe.upc.edu.bibflipbackend.booking.domain.services;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Booking;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.CreateBookingCommand;
import pe.upc.edu.bibflipbackend.booking.domain.model.commands.DeleteBookingCommand;

import java.util.Optional;

public interface BookingCommandService {
    Optional<Booking> handle(CreateBookingCommand command);
    void handle(DeleteBookingCommand command);
}
