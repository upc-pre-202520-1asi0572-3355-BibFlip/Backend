package pe.upc.edu.bibflipbackend.booking.domain.services;

import pe.upc.edu.bibflipbackend.booking.domain.model.aggregates.Booking;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsByIdClientQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetAllBookingsQuery;
import pe.upc.edu.bibflipbackend.booking.domain.model.queries.GetBookingByIdQuery;

import java.util.List;
import java.util.Optional;

public interface BookingQueryService {
    Optional<Booking> handle(GetBookingByIdQuery query);
    List<Booking> handle(GetAllBookingsQuery query);
    List<Booking> handle(GetAllBookingsByIdClientQuery query);
}
