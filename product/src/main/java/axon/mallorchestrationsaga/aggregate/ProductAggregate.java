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
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;

    private String productName;
    private Integer stock;
    private String orderId;

    public ProductAggregate() {}

    @CommandHandler
    public void handle(DecreaseStockCommand command) {
        StockDecreasedEvent event = new StockDecreasedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    @CommandHandler
    public ProductAggregate(RegisterCommand command) {
        ProductRegisteredEvent event = new ProductRegisteredEvent();
        BeanUtils.copyProperties(command, event);

        //TODO: check key generation is properly done
        if (event.getProductId() == null) event.setProductId(createUUID());

        apply(event);
    }

    @CommandHandler
    public void handle(IncreaseStockCommand command) {
        StockIncreasedEvent event = new StockIncreasedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    private String createUUID() {
        return UUID.randomUUID().toString();
    }

    @EventSourcingHandler
    public void on(StockDecreasedEvent event) {
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(ProductRegisteredEvent event) {
        BeanUtils.copyProperties(event, this);
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(StockIncreasedEvent event) {
        //TODO: business logic here

    }
}
