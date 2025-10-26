package pe.upc.edu.bibflipbackend.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record SignUpResource(
        String username,

        String password,

        List<String> roles,

        @NotBlank(message = "El email es requerido")
        @Email(message = "Formato de email inválido")
        @Pattern(regexp = "^u\\d{2}[A-Za-z0-9]{6,7}@upc\\.edu\\.pe$", message = "El email debe tener el formato u20xxxxxxx@upc.edu.pe")
        String email
) { }
