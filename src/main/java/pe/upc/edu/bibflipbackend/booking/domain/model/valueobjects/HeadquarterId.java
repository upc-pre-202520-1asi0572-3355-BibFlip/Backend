package pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects;

import pe.upc.edu.bibflipbackend.shared.application.exceptions.InvalidValueException;
import jakarta.persistence.Embeddable;

@Embeddable
public record HeadquarterId(Long headquarterId) {
    public HeadquarterId {
        if (headquarterId == null) {
            throw new InvalidValueException("HeadquarterId cannot be null");
        }
        if (headquarterId < 0) {
            throw new InvalidValueException("HeadquarterId cannot be negative");
        }
    }

    public HeadquarterId() {
        this(0L);
    }
}
