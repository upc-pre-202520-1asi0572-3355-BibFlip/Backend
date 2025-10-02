package pe.upc.edu.bibflipbackend.booking.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SingleCubicleAvailabilitySlotsGeneratedEvent extends ApplicationEvent {

    private final Long cubicleId;

     public SingleCubicleAvailabilitySlotsGeneratedEvent(Object source, Long cubicleId) {
        super(source);
        this.cubicleId = cubicleId;
    }
}
