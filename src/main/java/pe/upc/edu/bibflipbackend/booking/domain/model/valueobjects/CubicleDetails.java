package pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects;

import pe.upc.edu.bibflipbackend.shared.application.exceptions.InvalidValueException;
import jakarta.persistence.Embeddable;

@Embeddable
public record CubicleDetails(Integer cubicleNumber, Integer seats) {
    public CubicleDetails {
        if (cubicleNumber < 0) {
            throw new InvalidValueException("Cubicle number cannot be negative");
        }
        if (seats <= 0) {
            throw new InvalidValueException("Seats must be greater than zero");
        }
    }

    public CubicleDetails() {
        this(0, 0);
    }
}
