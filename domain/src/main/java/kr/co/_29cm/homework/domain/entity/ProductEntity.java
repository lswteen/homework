package kr.co._29cm.homework.domain.entity;


import kr.co_29cm.homework.exception.SoldOutException;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="product")
public class ProductEntity {
    @Id
    @Comment("옵션 ID")
    @Column(name="product_id")
    private Long productId;
    private String name;
    private Double price;
    private Integer quantity;

    public ProductEntity(Long productId, String name, Double price, Integer quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public void decreaseQuantity(Integer quantity) {
        validateQuantityCount(quantity);
        this.quantity -= quantity;
    }

    private void validateQuantityCount(Integer quantity) {
        if (quantity > this.getQuantity()) {
            throw new SoldOutException();
        }
    }
}
