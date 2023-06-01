package kr.co._29cm.homework.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="product")
public class ProductEntity {
    @Id
    @Column(name="product_id")
    private Long productId;

    private String name;

    private Double price;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private StockEntity stockEntity;

    public ProductEntity(Long productId, String name, Double price, StockEntity stockEntity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stockEntity = stockEntity;
    }
}
