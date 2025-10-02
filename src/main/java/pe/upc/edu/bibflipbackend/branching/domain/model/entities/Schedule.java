package pe.upc.edu.bibflipbackend.branching.domain.model.entities;

import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.BusinessHours;
import pe.upc.edu.bibflipbackend.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Entity
public class Schedule extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private BusinessHours businessHours;

    // Intervalo en minutos (ej. 30 o 60 minutos)
    @Column(nullable = false)
    private Integer intervalMinutes;

    protected Schedule() {
        // Constructor para JPA
    }

    public Schedule(LocalTime openingTime, LocalTime closingTime, Integer intervalMinutes) {
        this.businessHours = new BusinessHours(openingTime, closingTime);
        this.intervalMinutes = intervalMinutes;
    }

    public Schedule(BusinessHours businessHours, Integer intervalMinutes) {
        this.businessHours = businessHours;
        this.intervalMinutes = intervalMinutes;
    }

    // Aquí se pueden agregar métodos de dominio para validar cambios (por ejemplo, evitar modificar el intervalo si hay reservas existentes).


}
