package kr.co._29cm.homework.shell.service;

import kr.co._29cm.homework.domain.service.ProductService;
import kr.co._29cm.homework.domain.service.StockService;
import kr.co._29cm.homework.shell.mapper.ProductQueryMapper;
import kr.co._29cm.homework.shell.prompt.OrderPromptStrings;
import kr.co._29cm.homework.shell.request.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductAppService {
    private final ProductService productService;
    private final StockService stockService;
    private final ProductQueryMapper queryMapper = ProductQueryMapper.INSTANCE;

    public List<Product> findByProducts(){
        return queryMapper.toProducts(productService.findByProducts());
    }

    public Product findByProductId(Long productId){
        return queryMapper.toProduct(productService.findByProductId(productId));
    }

    public void decreaseProductQuantity(Map<Long,Integer> productQuantities){
        stockService.decreaseStock(productQuantities);
    }

    public void printProductInfo() {
        var productList = findByProducts();
        System.out.printf("%-10s %-60s %-12s %-10s%n",
                OrderPromptStrings.PRODUCT_NUMBER_LABEL, OrderPromptStrings.PRODUCT_NAME_LABEL,
                OrderPromptStrings.SELLING_PRICE_LABEL, OrderPromptStrings.STOCK_QUANTITY_LABEL);

        productList.stream().forEach(
                product -> System.out.printf("%-10s %-60s %-12s %-10s%n",
                        product.getProductId(), product.getName(),
                        removeDecimalZero(product.getPrice()), product.getStock().getQuantity()
                )
        );
    }

    private String removeDecimalZero(Double value) {
        var formattedValue = String.format("%.1f", value);
        return formattedValue.endsWith(".0") ? formattedValue.replace(".0", "") : formattedValue;
    }

}
