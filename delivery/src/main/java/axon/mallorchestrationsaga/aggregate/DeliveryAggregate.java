package axon.mallorchestrationsaga.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.*;

import axon.mallorchestrationsaga.command.*;
import axon.mallorchestrationsaga.event.*;
import axon.mallorchestrationsaga.query.*;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@Data
@ToString
public class DeliveryAggregate {

    @AggregateIdentifier
    private String deliveryId;

    private String userId;
    private String address;
    private String orderId;
    private String productId;
    private Integer qty;
    private String status;

    public DeliveryAggregate() {}

    @CommandHandler
    public void handle(StartDeliveryCommand command) {
        DeliveryStartedEvent event = new DeliveryStartedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    private String createUUID() {
        return UUID.randomUUID().toString();
    }

    @EventSourcingHandler
    public void on(DeliveryStartedEvent event) {
        //TODO: business logic here

    }
}
