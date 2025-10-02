package pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects;

import pe.upc.edu.bibflipbackend.shared.application.exceptions.InvalidValueException;
import jakarta.persistence.Embeddable;

@Embeddable
public record UserId(Long clientId) {
    public UserId {
        if (clientId < 0) {
            throw new InvalidValueException("Profile profileId cannot be negative");
        }
    }

    public UserId() {
        this(0L);
    }
}
