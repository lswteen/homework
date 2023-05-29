package kr.co._29cm.homework.shell.request;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@ToString
public class Order {
    private UUID orderId;
    private ProductEntity product;
    private int quantity;

    public Order(ProductEntity product, int quantity) {
        this.orderId = UUID.randomUUID();  // 주문 ID 생성
        this.product = product;
        this.quantity = quantity;
    }

    public UUID getOrderId() {
        return orderId;
    }
    public ProductEntity getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
        log.info("this.quantity");
    }


}
