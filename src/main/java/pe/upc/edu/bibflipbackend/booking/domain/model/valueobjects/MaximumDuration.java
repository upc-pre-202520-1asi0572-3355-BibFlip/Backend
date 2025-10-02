package pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects;

import pe.upc.edu.bibflipbackend.shared.application.exceptions.InvalidValueException;

public record MaximumDuration(Integer value) {
    public MaximumDuration {
        if (value == null || value <= 0) {
            throw new InvalidValueException("Maximum duration must be a positive integer.");
        }
        if (value > 5) {
            throw new InvalidValueException("Maximum duration cannot exceed 5 hours.");
        }
    }
}
