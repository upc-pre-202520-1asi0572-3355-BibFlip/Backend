package pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects;

import pe.upc.edu.bibflipbackend.shared.application.exceptions.InvalidValueException;
import jakarta.persistence.Embeddable;

@Embeddable
public record ContactNumbers(String landlinePhone, String mobilePhone) {
    public ContactNumbers {
        if (landlinePhone == null || landlinePhone.isBlank()) {
            throw new InvalidValueException("landlinePhone is null or empty");
        }
        if (mobilePhone == null || mobilePhone.isBlank()) {
            throw new InvalidValueException("mobilePhone is null or empty");
        }
    }
}
