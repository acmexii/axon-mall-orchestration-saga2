package axon.mallorchestrationsaga.command;

import lombok.Data;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
@Data
public class OrderCancelCommand {

    @TargetAggregateIdentifier
    private String orderId;
}
