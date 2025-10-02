package pe.upc.edu.bibflipbackend.branching.domain.model.entities;

import pe.upc.edu.bibflipbackend.branching.domain.model.aggregates.Headquarter;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.UserId;
import pe.upc.edu.bibflipbackend.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table
public class HeadquarterSupervisor extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "headquarter_id", nullable = false)
    private Headquarter headquarter;

    @Column(nullable = false)
    private UserId userId;

    protected HeadquarterSupervisor() {
        // Constructor para JPA
    }

    public HeadquarterSupervisor(Headquarter headquarter, UserId userId) {
        this.headquarter = headquarter;
        this.userId = userId;
    }
}