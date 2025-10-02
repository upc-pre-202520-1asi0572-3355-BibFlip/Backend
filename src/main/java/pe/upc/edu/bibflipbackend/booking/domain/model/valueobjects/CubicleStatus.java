package pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects;

/**
 * Enumeración que representa el estado de una mesa en el sistema.
 * AVAILABLE: Mesa disponible para reservas o uso.
 * RESERVED: Mesa reservada pero no ocupada actualmente.
 * OCCUPIED: Mesa ocupada por clientes.
 * DELETED: Mesa marcada como eliminada (borrado lógico).
 */
public enum CubicleStatus {
    AVAILABLE,
    RESERVED,
    OCCUPIED,
    DELETED
}
