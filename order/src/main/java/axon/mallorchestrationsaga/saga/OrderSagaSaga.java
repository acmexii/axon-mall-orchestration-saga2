package axon.mallorchestrationsaga.saga;

import axon.mallorchestrationsaga.command.*;
import axon.mallorchestrationsaga.event.*;
import java.util.UUID;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
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
        StartDeliveryCommand command = new StartDeliveryCommand();
        command.setOrderId(event.getOrderId());
        command.setProductId(event.getProductId());
        command.setQty(event.getQty());
        command.setUserId(event.getUserId());

        //SagaLifecycle.associateWith("deliveryId", "deliveryId");

        commandGateway
            .send(command)
            .exceptionally(ex -> {
                OrderCancelCommand orderCancelCommand = new OrderCancelCommand();
                orderCancelCommand.setOrderId(event.getOrderId());
                return commandGateway.send(orderCancelCommand);
            });
    }

    @SagaEventHandler(associationProperty = "deliveryId")
    public void onDeliveryStarted(DeliveryStartedEvent event) {
        DecreaseStockCommand command = new DecreaseStockCommand();
        command.setProductId(event.getProductId());
        command.setStock(event.getQty());
        command.setOrderId(event.getOrderId());

        //SagaLifecycle.associateWith("productId", "productId");

        commandGateway
            .send(command)
            .exceptionally(ex -> {
                CancelDeliveryCommand cancelDeliveryCommand = new CancelDeliveryCommand();
                //
                cancelDeliveryCommand.setDeliveryId(event.getDeliveryId());
                return commandGateway.send(cancelDeliveryCommand);
            });
    }

    @SagaEventHandler(associationProperty = "productId")
    public void onStockDecreased(StockDecreasedEvent event) {
        UpdateStatusCommand command = new UpdateStatusCommand();
        command.setOrderId(event.getOrderId());
        commandGateway.send(command);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void onOrderCompleted(OrderCompletedEvent event) {}
}
