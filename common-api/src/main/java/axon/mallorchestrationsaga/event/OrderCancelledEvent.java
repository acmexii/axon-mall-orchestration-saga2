package axon.mallorchestrationsaga.event;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderCancelledEvent {

    private String orderId;
    private String productName;
    private String productId;
    private String status;
    private Integer qty;
    private String userId;
}
