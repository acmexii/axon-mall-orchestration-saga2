package axon.mallorchestrationsaga.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StockDecreasedEvent {

    private String productId;
    private String productName;
    private Integer stock;
    private String orderId;
}
