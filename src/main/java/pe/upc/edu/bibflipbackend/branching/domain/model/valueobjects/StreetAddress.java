package pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects;

import pe.upc.edu.bibflipbackend.shared.application.exceptions.InvalidValueException;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class StreetAddress {
    private String street;
    private String number;
    private String city;
    private String postalCode;
    private String country;

    public StreetAddress(String street, String number, String city, String postalCode, String country) {
        // Validaciones
        if (street == null || street.isBlank())
            throw new InvalidValueException("Street cannot be null or empty");
        if (city == null || city.isBlank())
            throw new InvalidValueException("City cannot be null or empty");
        if (postalCode == null || postalCode.isBlank())
            throw new InvalidValueException("PostalCode cannot be null or empty");
        if (country == null || country.isBlank())
            throw new InvalidValueException("Country cannot be null or empty");

        // La calle solo debe tener letras, espacios y puntos
        if(!street.matches("^[a-zA-Z\\s.]+$"))
            throw new InvalidValueException("Street contains illegal characters");

        this.street = street;
        this.number = number;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    public StreetAddress(String street, String city, String postalCode, String country) {
        this(street, "", city, postalCode, country);
    }

    public String getAddress() {
        return String.format("%s %s, %s, %s, %s",
                street != null ? street : "",
                number != null ? number : "",
                city != null ? city : "",
                postalCode != null ? postalCode : "",
                country != null ? country : "");
    }
}