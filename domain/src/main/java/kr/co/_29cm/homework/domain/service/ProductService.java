package kr.co._29cm.homework.domain.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

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
            var productEntity = entityManager
                    .find(ProductEntity.class, productId, LockModeType.PESSIMISTIC_WRITE);

            if (productEntity == null) {
                throw new ProductNotFoundException(productId);
            }

            if (productEntity.getQuantity() < quantity) {
                throw new SoldOutException();
            }

            productEntity.decreaseQuantity(quantity);
            productRepository.saveAndFlush(productEntity);
        }
    }
}
