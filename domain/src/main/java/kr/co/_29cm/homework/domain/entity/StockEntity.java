package kr.co._29cm.homework.domain.entity;

import jakarta.persistence.*;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="stock")
public class StockEntity {
    @Id
    @Comment("재고 ID")
    @Column(name="stock_id")
    private Long stockId;

    @Comment("재고 수량")
    private Integer quantity;

    public StockEntity(Long stockId, Integer quantity, ProductEntity product) {
        this.stockId = stockId;
        this.quantity = quantity;
        this.product = product;
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
