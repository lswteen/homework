package kr.co._29cm.homework.domain.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.entity.StockEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co._29cm.homework.domain.repository.StockRepository;
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
    private final StockRepository stockRepository;
    private final EntityManager entityManager;

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
    public ProductEntity createProductWithStock(Long productId, String name, Double price, Integer initialQuantity) {
        ProductEntity productEntity = new ProductEntity(productId, name, price, null);
        // 해당 상품의 재고 생성
        StockEntity stockEntity = new StockEntity(productId, initialQuantity, productEntity);
        // 상품에 재고 정보 설정
        productEntity.setStockEntity(stockEntity);

        // 상품과 재고 저장
        productRepository.save(productEntity);
        stockRepository.save(stockEntity);

        // 모든 변경 사항을 데이터베이스에 즉시 반영
        entityManager.flush();

        return productEntity;
    }
}
