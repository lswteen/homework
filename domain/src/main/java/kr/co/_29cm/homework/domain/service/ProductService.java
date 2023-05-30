package kr.co._29cm.homework.domain.service;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductEntity> findByProducts(){
        return productRepository.findAll();
    }

    @Transactional
    public ProductEntity save(ProductEntity productEntity){
        return productRepository.save(productEntity);
    }

    @Transactional(readOnly = true)
    public ProductEntity findByProductId(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(()-> new IllegalArgumentException("해당 ID상품을 찾을수 없습니다." + productId));
    }

    @Transactional
    public void decreaseProductQuantity(Long productId, int quantity) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID상품을 찾을수 없습니다." + productId));

        product.decreaseQuantity(quantity);
    }
}
