package kr.co._29cm.homework.shell.request;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class Order {
    private UUID orderId;
    private Product product;
    private int quantity;

    public Order(Product product, int quantity) {
        this.orderId = UUID.randomUUID();
        this.product = product;
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

}
