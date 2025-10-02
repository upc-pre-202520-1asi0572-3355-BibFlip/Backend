package pe.upc.edu.bibflipbackend.booking.domain.model.events;

import pe.upc.edu.bibflipbackend.booking.domain.model.valueobjects.HeadquarterId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalTime;

@Getter
public class WeeklyCubicleAvailabilitySlotsGeneratedEvent extends ApplicationEvent {

    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final Integer intervalMinutes;
    private final HeadquarterId headquarterId;

    public WeeklyCubicleAvailabilitySlotsGeneratedEvent(Object source, LocalTime openingTime, LocalTime closingTime, Integer intervalMinutes, HeadquarterId headquarterId) {
        super(source);
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.intervalMinutes = intervalMinutes;
        this.headquarterId = headquarterId;
    }
}
