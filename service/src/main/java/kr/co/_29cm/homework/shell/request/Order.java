package kr.co._29cm.homework.shell.request;

import lombok.ToString;

import java.util.UUID;

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

    public UUID getOrderId() {
        return orderId;
    }
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

}
