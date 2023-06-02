package kr.co._29cm.homework.shell.service;

import kr.co._29cm.homework.shell.request.Order;
import kr.co._29cm.homework.shell.request.Product;
import kr.co._29cm.homework.shell.request.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OrderAppServiceTest {
    private static final double DELIVERY_FEE = 2500; // 배송비 설정
    private final OrderAppService orderAppService;
    public OrderAppServiceTest(@Autowired OrderAppService orderAppService) {
        this.orderAppService = orderAppService;
    }

    private Product createProduct(Long productId, String name, Double price, Integer quantity) {
        return Product.builder()
                .productId(productId)
                .name(name)
                .price(price)
                .stock(Stock.builder()
                        .productId(productId)
                        .quantity(quantity)
                        .build())
                .build();
    }

    private Product Product_1;
    private Product Product_2;
    private Product Product_3;

    @BeforeEach
    public void setUp() {
        Product_1 = createProduct(779989L, "버드와이저 HOME DJing 굿즈 세트", 35000D, 43);
        Product_2 = createProduct(748943L, "디오디너리 데일리 세트 (Daily set)", 19000D, 89);
        Product_3 = createProduct(648418L, "BS 02-2A DAYPACK 26 (BLACK)", 238000D, 5);
    }

    @Test
    public void add_order() {
        Order order1 = new Order(Product_1, 5, "user1");
        orderAppService.addOrder(order1);
        assertThat(orderAppService.getOrders().get(0).equals(order1));
    }

    @Test
    public void order_quantity_for_product_stock_sum() {
        Order order1 = new Order(Product_1, 5, "user1");
        Order order2 = new Order(Product_1, 3, "user2");
        orderAppService.addOrder(order1);
        orderAppService.addOrder(order2);
        assertThat(orderAppService.getTotalQuantityForProduct(Product_1.getProductId())).isEqualTo(8);
    }

    @Test
    public void clear_order() {
        Order order = new Order(Product_1, 5, "user1");
        orderAppService.addOrder(order);
        orderAppService.clearOrders();
        assertThat(orderAppService.getOrders()).isEmpty();
    }

    private double deliveryCharge(double totalOrderPrice) {
        if (totalOrderPrice < 50000 && totalOrderPrice > 0) {
            totalOrderPrice += DELIVERY_FEE;
        }
        return totalOrderPrice;
    }

    @Test
    void delivery_charge_when_total_orderprice_40000() {
        // Arrange
        double totalOrderPrice = 40000;
        double expected = totalOrderPrice + DELIVERY_FEE;

        // Act
        double result = deliveryCharge(totalOrderPrice);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void delivery_charge_when_total_orderprice_60000() {
        // Arrange
        double totalOrderPrice = 60000;
        double expected = totalOrderPrice;

        // Act
        double result = deliveryCharge(totalOrderPrice);

        // Assert
        assertEquals(expected, result);
    }

}