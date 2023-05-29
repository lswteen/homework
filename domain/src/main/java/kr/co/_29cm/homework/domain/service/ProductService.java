package kr.co._29cm.homework.domain.service;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
}
