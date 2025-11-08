package pe.upc.edu.bibflipbackend.booking.interfaces.rest.resources;

public record UpdateAvailabilitySlotStatusResource(
        String status  // "AVAILABLE", "RESERVED", "OCCUPIED"
) { }
