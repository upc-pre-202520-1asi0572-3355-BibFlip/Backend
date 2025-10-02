package pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects;

import pe.upc.edu.bibflipbackend.shared.application.exceptions.InvalidValueException;
import jakarta.persistence.Embeddable;

import java.time.LocalTime;
import java.util.Objects;

@Embeddable
public record TimeSlot(LocalTime startTime, LocalTime endTime) {
    public TimeSlot {
        if (startTime == null || endTime == null) {
            throw new InvalidValueException("Start time and end time cannot be null");
        }
        if (startTime.isAfter(endTime)) {
            throw new InvalidValueException("Start time must be before end time");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot timeSlot)) return false;
        return Objects.equals(startTime, timeSlot.startTime) &&
                Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }
    public TimeSlot() {
        this(LocalTime.of(0, 0), LocalTime.of(23, 59));
    }
}
