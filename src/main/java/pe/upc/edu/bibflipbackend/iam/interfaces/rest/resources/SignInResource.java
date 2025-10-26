package pe.upc.edu.bibflipbackend.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import pe.upc.edu.bibflipbackend.iam.domain.validators.InstitutionalEmailRequired;

@InstitutionalEmailRequired
public record SignInResource(String email, String password) { }
