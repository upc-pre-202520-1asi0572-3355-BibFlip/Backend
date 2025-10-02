package pe.upc.edu.bibflipbackend.branching.domain.model.aggregates;

import pe.upc.edu.bibflipbackend.branching.domain.model.commands.CreateHeadquarterCommand;
import pe.upc.edu.bibflipbackend.branching.domain.model.entities.Schedule;
import pe.upc.edu.bibflipbackend.branching.domain.model.valueobjects.*;
import pe.upc.edu.bibflipbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Entity
public class Headquarter extends AuditableAbstractAggregateRoot<Headquarter> {

    @Embedded
    private NameHeadquarter name;

    @Embedded
    private ContactNumbers contactNumbers;

    @Embedded
    private Coordinates coordinates;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "address_street")),
            @AttributeOverride(name = "number", column = @Column(name = "address_number")),
            @AttributeOverride(name = "city", column = @Column(name = "address_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "address_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "address_country"))})
    private StreetAddress address;

    public Headquarter() {
        // Default constructor for JPA
    }

    public Headquarter(CreateHeadquarterCommand command) {
        this.name = new NameHeadquarter(command.name());
        this.contactNumbers = new ContactNumbers(command.landlinePhone(), command.mobilePhone());
        this.coordinates = new Coordinates(command.latitude(), command.longitude());
        this.schedule = new Schedule(command.openingTime(), command.closingTime(), command.intervalMinutes());
        this.address = new StreetAddress(command.street(), command.number(), command.city(), command.postalCode(), command.country());
    }

    public LocalTime getOpeningTime() {
        return schedule.getBusinessHours().openingTime();
    }

    public LocalTime getClosingTime() {
        return schedule.getBusinessHours().closingTime();
    }

    public Integer getIntervalMinutes() {
        return schedule.getIntervalMinutes();
    }

}