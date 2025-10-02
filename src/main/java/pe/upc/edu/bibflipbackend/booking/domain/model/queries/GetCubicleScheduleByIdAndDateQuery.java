package pe.upc.edu.bibflipbackend.booking.domain.model.queries;

import java.time.LocalDate;

public record GetCubicleScheduleByIdAndDateQuery(Long cubicleId, LocalDate date) {
}
