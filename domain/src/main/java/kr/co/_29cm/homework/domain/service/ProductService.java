package kr.co._29cm.homework.domain.service;

import jakarta.persistence.OptimisticLockException;
import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co_29cm.homework.exception.ProductNotFoundException;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    public void decreaseProductQuantity(Map<Long, Integer> productQuantities, String userId) {
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            var productId = entry.getKey();
            var quantity = entry.getValue();
            try {
                var productEntity = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));

                if (productEntity.getQuantity() < quantity) {
                    throw new SoldOutException();
                }
                log.info("userId : {}, product : {}",userId, productEntity.toString());
                // 재고 차감
                productEntity.decreaseQuantity(quantity);
                productRepository.saveAndFlush(productEntity);
            } catch (OptimisticLockException e) {
                log.info("OptimisticLockException 발생. 충돌 Rock");
                throw e;
            }
        }
    }
}
