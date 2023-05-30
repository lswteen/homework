package kr.co._29cm.homework.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="product")
public class ProductEntity {
    @Id
    @Comment("옵션 ID")
    @Column(name="product_id")
    private Long productId;

    @Column(name="name")
    private String name;

    @Column(name="price")
    private Double price;

    @Column(name="quantity")
    private Integer quantity;

    public void decreaseQuantity(Integer quantity) {
        int restQuantity = this.quantity - quantity;
        if (restQuantity < 0) {
            throw new IllegalStateException("재고가 충분하지 않습니다.");
        }
        this.quantity = restQuantity;
    }
}
