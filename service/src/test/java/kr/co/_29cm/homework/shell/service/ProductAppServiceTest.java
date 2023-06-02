package kr.co._29cm.homework.shell.service;


import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co._29cm.homework.domain.repository.StockRepository;
import kr.co._29cm.homework.domain.service.StockService;
import kr.co._29cm.homework.shell.request.Product;
import kr.co._29cm.homework.shell.request.Stock;
import kr.co_29cm.homework.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductAppServiceTest {
    private final ProductAppService productAppService;
    private final StockService stockService;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    public ProductAppServiceTest(@Autowired ProductAppService productAppService,
                                 @Autowired StockService stockService,
                                 @Autowired ProductRepository productRepository,
                                 @Autowired StockRepository stockRepository) {
        this.productAppService = productAppService;
        this.stockService = stockService;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
    }

    private Product testProduct;

    @BeforeEach
    public void setUp() {
        testProduct = Product.builder()
                .productId(779989L)
                .name("버드와이저 HOME DJing 굿즈 세트")
                .price(35000D)
                .stock(Stock.builder()
                        .productId(779989L)
                        .quantity(43)
                        .build())
                .build();
    }

    @Test
    void find_by_products() {
        List<Product> products = productAppService.findByProducts();
        assertFalse(products.isEmpty(), "products should not be empty");
        assertTrue(products.contains(testProduct), "products should contain test product");
    }

    @Test
    void find_by_product() {
        Product product = productAppService.findByProductId(testProduct.getProductId());
        assertNotNull(product, "product should not be null");
        assertEquals(testProduct, product, "product should match test product");
        assertThat(testProduct.getProductId().equals(product.getProductId()));
    }

    private String removeDecimalZero(Double value) {
        var formattedValue = String.format("%.1f", value);
        return formattedValue.endsWith(".0") ? formattedValue.replace(".0", "") : formattedValue;
    }

    @Test
    void testRemoveDecimalZero_WhenHasDecimalZero() {
        // Arrange
        Double input = 10.0;
        String expected = "10";

        // Act
        String result = removeDecimalZero(input);

        // Assert
        assertThat(result.equals(expected));
    }

    @Test
    void testProductNotFoundException() {
        assertThrows(ProductNotFoundException.class, () -> {
            productAppService.findByProductId(999999L);
        });
    }

    @Test
    void decrease_product_quantity(){
        Map<Long,Integer> productQuantities = Map.of(768848L,3);
        Product beForProduct = productAppService.findByProductId(768848L);
        assertThat(beForProduct.getStock().getQuantity().equals(45));

        stockService.decreaseStock(productQuantities);

        Product afterProduct = productAppService.findByProductId(768848L);
        assertThat(afterProduct.getStock().getQuantity().equals(42));
    }


}