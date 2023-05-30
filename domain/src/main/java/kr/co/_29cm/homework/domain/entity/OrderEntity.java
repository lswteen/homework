package kr.co._29cm.homework.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="orders") //
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name="quantity")
    private Integer quantity;

    // 주문자에 대한 정보를 위한 필드 추가 (예시: 사용자 ID)
    @Column(name="user_id")
    private String userId;

    public OrderEntity(ProductEntity product, Integer quantity, String userId) {
        this.product = product;
        this.quantity = quantity;
        this.userId = userId;
        product.decreaseQuantity(quantity);
   }
}
