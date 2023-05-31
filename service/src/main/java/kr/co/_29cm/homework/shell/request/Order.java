package kr.co._29cm.homework.shell.request;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Order {
    private Long id;
    private Product product;
    private Integer quantity;
    private String userId;

    public Order(Product product, int quantity, String userId) {
//        this.id = null;  // Initially, the order ID is unknown
        this.product = product;
        this.quantity = quantity;
        this.userId = userId;
    }

//    public void addQuantity(int quantity) {
//        this.quantity += quantity;
//    }

}
