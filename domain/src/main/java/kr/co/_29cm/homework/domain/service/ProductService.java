package kr.co._29cm.homework.domain.service;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co_29cm.homework.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductEntity> findByProducts(){
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ProductEntity findByProductId(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException(productId));
    }

    @Transactional
    public ProductEntity createProductWithStock(ProductEntity productEntity) {
        return productRepository.save(productEntity);
    }
}
