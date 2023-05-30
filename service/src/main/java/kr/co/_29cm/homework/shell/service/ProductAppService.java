package kr.co._29cm.homework.shell.service;

import kr.co._29cm.homework.domain.service.ProductService;
import kr.co._29cm.homework.shell.mapper.ProductQueryMapper;
import kr.co._29cm.homework.shell.request.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductAppService {
    private final ProductService productService;
    private final ProductQueryMapper queryMapper = ProductQueryMapper.INSTANCE;

    public ProductAppService(ProductService productService) {
        this.productService = productService;
    }

    public List<Product> findByProducts(){
        return queryMapper.toProducts(productService.findByProducts());
    }

    public Product findByProductId(Long productId){
        return queryMapper.toProduct(productService.findByProductId(productId));
    }

    public void decreaseProductQuantity(Long productId, int quantity) {
        productService.decreaseProductQuantity(productId, quantity);
    }
}
