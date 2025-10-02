package pe.upc.edu.bibflipbackend.branching.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

public record SupervisorResource(

        @Schema(description = "ID of the supervisor", example = "1")
        Long id,

        @Schema(description = "Username of the supervisor", example = "john.doe")
        String username,

        @Schema(description = "Headquarter ID", example = "1")
        Long headquarterId
) {}