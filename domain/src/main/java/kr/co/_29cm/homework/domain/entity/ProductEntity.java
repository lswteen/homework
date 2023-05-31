package kr.co._29cm.homework.domain.entity;

import jakarta.persistence.*;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Builder
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Version
    private int version;

    public void decreaseQuantity(Integer quantity) {
        int restQuantity = this.quantity - quantity;
        if (restQuantity < 0) {
            throw new SoldOutException();
        }
        this.quantity = restQuantity;
    }
}
