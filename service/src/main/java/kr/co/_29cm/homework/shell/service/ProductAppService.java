package kr.co._29cm.homework.shell.service;

import kr.co._29cm.homework.domain.service.ProductService;
import kr.co._29cm.homework.domain.service.StockService;
import kr.co._29cm.homework.shell.mapper.ProductQueryMapper;
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
        stockService.objectOptimisticLockingdecreaseStock(productQuantities);
    }

}
