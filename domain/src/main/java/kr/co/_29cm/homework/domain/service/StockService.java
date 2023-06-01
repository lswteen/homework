package kr.co._29cm.homework.domain.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import kr.co._29cm.homework.domain.entity.StockEntity;
import kr.co._29cm.homework.domain.repository.StockRepository;
import kr.co_29cm.homework.exception.SoldOutException;
import kr.co_29cm.homework.exception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final EntityManager entityManager;

    @Transactional
    public void decreaseStock(Map<Long, Integer> productQuantities) {
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            var productId = entry.getKey();
            var quantity = entry.getValue();

            // Pessimistic lock을 사용하여 동시성 문제를 방지합니다.
            var stockEntity = entityManager
                    .find(StockEntity.class, productId, LockModeType.PESSIMISTIC_WRITE);

            if (stockEntity == null) {
                throw new StockNotFoundException(productId);
            }

            if (stockEntity.getQuantity() < quantity) {
                throw new SoldOutException();
            }

            stockEntity.decreaseQuantity(quantity);
            stockRepository.save(stockEntity);
        }
    }

    @Transactional(readOnly = true)
    public StockEntity findByProductId(Long productId) {
        return stockRepository.findById(productId)
                .orElseThrow(() -> new StockNotFoundException(productId));
    }

    @Transactional(readOnly = true)
    public List<StockEntity> findAll() {
        return stockRepository.findAll();
    }
}
