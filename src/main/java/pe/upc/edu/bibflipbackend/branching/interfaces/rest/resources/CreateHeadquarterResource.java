package pe.upc.edu.bibflipbackend.branching.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CreateHeadquarterResource(
        @Schema(description = "Headquarter name", example = "Main Branch")
        @NotBlank(message = "Name is mandatory")
        @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
        String name,

        @Schema(description = "Landline phone number with country code",
                example = "+5112345678",
                pattern = "^\\+51[1-9]\\d{7}$")
        @NotBlank(message = "Landline phone is mandatory")
        @Pattern(regexp = "^\\+51[1-9]\\d{7}$",
                message = "Invalid Peruvian landline format. Valid format: +51XXXXXXXX")
        String landlinePhone,

        @Schema(description = "Mobile phone number with country code",
                example = "013152700",
                pattern = "^(?:01|041|043|083|054|066|076|084|067|062|056|065|064|044|074|082|053|063|073|051|042|052|072|061)\\d{6}$")
        @NotBlank(message = "Mobile phone is mandatory")
        @Pattern(
                regexp = "^(?:01|041|043|083|054|066|076|084|067|062|056|065|064|044|074|082|053|063|073|051|042|052|072|061)\\d{6}$",
                message = "Invalid Peruvian landline format. Examples: 013152700 (Lima), 054234567 (Arequipa)"
        )
        String mobilePhone,

        @Schema(description = "Latitude coordinate (Peru range)", example = "-12.046373")
        @NotNull(message = "Latitude is mandatory")
        @DecimalMin(value = "-18.0", message = "Latitude must be within Peru's range (-18.0 to 0.0)")
        @DecimalMax(value = "0.0", message = "Latitude must be within Peru's range (-18.0 to 0.0)")
        Double latitude,

        @Schema(description = "Longitude coordinate (Peru range)", example = "-77.042754")
        @NotNull(message = "Longitude is mandatory")
        @DecimalMin(value = "-81.3", message = "Longitude must be within Peru's range (-81.3 to -68.7)")
        @DecimalMax(value = "-68.7", message = "Longitude must be within Peru's range (-81.3 to -68.7)")
        Double longitude,

        @Schema(description = "Street name", example = "Av. Javier Prado")
        @NotBlank(message = "Street is mandatory")
        @Size(max = 100, message = "Street cannot exceed 100 characters")
        String street,

        @Schema(description = "Street number", example = "1234")
        @NotBlank(message = "Street number is mandatory")
        @Size(max = 20, message = "Street number cannot exceed 20 characters")
        String number,

        @Schema(description = "City name", example = "Lima")
        @NotBlank(message = "City is mandatory")
        @Size(max = 50, message = "City cannot exceed 50 characters")
        String city,

        @Schema(description = "Postal code", example = "15023")
        @NotBlank(message = "Postal code is mandatory")
        @Size(max = 20, message = "Postal code cannot exceed 20 characters")
        String postalCode,

        @Schema(description = "Country name", example = "Peru")
        @NotBlank(message = "Country is mandatory")
        @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
        String country,

        @Schema(description = "Opening time in HH:mm format", example = "08:00")
        @NotBlank(message = "Opening time is mandatory")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
                message = "Invalid opening time format. Use HH:mm (24-hour format)")
        String openingTime,

        @Schema(description = "Closing time in HH:mm format", example = "18:30")
        @NotBlank(message = "Closing time is mandatory")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
                message = "Invalid closing time format. Use HH:mm (24-hour format)")
        String closingTime,

        @Schema(description = "Service interval in minutes", example = "30")
        @NotNull(message = "Interval minutes is mandatory")
        @Min(value = 1, message = "Interval must be at least 1 minute")
        @Max(value = 1440, message = "Interval cannot exceed 1440 minutes (24 hours)")
        Integer intervalMinutes
) {}