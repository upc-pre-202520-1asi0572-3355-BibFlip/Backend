package pe.upc.edu.bibflipbackend.booking.domain.model.queries;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Query para buscar un booking activo en un cubículo específico
 * en una fecha y hora determinada.
 *
 * Usado por el IoT Edge API para cancelar bookings cuando
 * el sensor detecta que la silla está disponible.
 */
public record GetBookingByCubicleAndTimeQuery(
        Long cubicleId,
        LocalDate date,
        LocalTime time
) {
    public GetBookingByCubicleAndTimeQuery {
        if (cubicleId == null || cubicleId <= 0) {
            throw new IllegalArgumentException("Cubicle ID must be positive");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (time == null) {
            throw new IllegalArgumentException("Time cannot be null");
        }
    }
}