package axon.mallorchestrationsaga.saga;

import axon.mallorchestrationsaga.command.*;
import axon.mallorchestrationsaga.event.*;
import java.util.UUID;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Saga
@ProcessingGroup("OrderSagaSaga")
public class OrderSagaSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void onOrderPlaced(OrderPlacedEvent event) {
        DecreaseStockCommand command = new DecreaseStockCommand();
        command.setProductId(event.getProductId());
        command.setStock(event.getQty());

        //SagaLifecycle.associateWith("paymentId", paymentId);

        commandGateway
            .send(command)
            .exceptionally(ex -> {
                OrderCancelCommand orderCancelCommand = new OrderCancelCommand();
                //
                return commandGateway.send(orderCancelCommand);
            });
    }

    @SagaEventHandler(associationProperty = "#correlation-key")
    public void onStockDecreased(StockDecreasedEvent event) {
        StartDeliveryCommand command = new StartDeliveryCommand();

        commandGateway
            .send(command)
            .exceptionally(ex -> {
                IncreaseStockCommand increaseStockCommand = new IncreaseStockCommand();
                //
                return commandGateway.send(increaseStockCommand);
            });
    }

    @SagaEventHandler(associationProperty = "#correlation-key")
    public void onDeliveryStarted(DeliveryStartedEvent event) {
        UpdateStatusCommand command = new UpdateStatusCommand();

        commandGateway.send(command);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "#correlation-key")
    public void onOrderCompleted(OrderCompletedEvent event) {}
}
