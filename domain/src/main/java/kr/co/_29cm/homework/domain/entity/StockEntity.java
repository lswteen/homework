package kr.co._29cm.homework.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="stock")
public class StockEntity {
    @Id
    @Column(name = "product_id")
    private Long productId;

    private Integer quantity;

    @Version
    private Long version;

    public StockEntity(Long productId, Integer quantity) {
        this.productId = productId;
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
