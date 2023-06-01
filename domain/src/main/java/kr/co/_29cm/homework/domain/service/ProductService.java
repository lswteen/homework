package kr.co._29cm.homework.domain.service;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co_29cm.homework.exception.ProductNotFoundException;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
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
    public List<ProductEntity> decreaseProductQuantity(Map<Long, Integer> productQuantities, String userId) {
        List<ProductEntity> productEntities = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            var productId = entry.getKey();
            var quantity = entry.getValue();

//            var productEntity = productRepository.findById(productId)
//                    .orElseThrow(() -> new ProductNotFoundException(productId));

            // Acquire a PESSIMISTIC_WRITE lock on the product
            var productEntity = entityManager
                    .find(ProductEntity.class, productId, LockModeType.PESSIMISTIC_WRITE);

            if (productEntity == null) {
                throw new ProductNotFoundException(productId);
            }

            if (productEntity.getQuantity() < quantity) {
                throw new SoldOutException();
            }
            log.info("userId : {}, product : {}",userId, productEntity.toString());
            // 재고 차감
            productEntity.decreaseQuantity(quantity);
            //productRepository.save(productEntity);
            productRepository.saveAndFlush(productEntity);
            productEntities.add(productEntity);

        }
        return productEntities;
    }
}
