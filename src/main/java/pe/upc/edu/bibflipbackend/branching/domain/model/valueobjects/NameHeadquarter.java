package pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects;

import pe.upc.edu.bibflipbackend.shared.application.exceptions.InvalidValueException;
import jakarta.persistence.Embeddable;

@Embeddable
public record NameHeadquarter(String name) {
    public NameHeadquarter {
        if (name == null || name.isBlank()) {
            throw new InvalidValueException("name is null or empty");
        }
        if(!name.matches("^[a-zA-Z\\s]+$"))        {
            throw new InvalidValueException("name contains illegal characters");
        }
    }
}
