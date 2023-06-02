package kr.co._29cm.homework.domain.service;

import kr.co._29cm.homework.domain.repository.StockRepository;
import kr.co_29cm.homework.exception.SoldOutException;
import kr.co_29cm.homework.exception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    @Transactional
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 5)
    public void objectOptimisticLockingDecreaseStock(Map<Long, Integer> productQuantities) {
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            var productId = entry.getKey();
            var quantity = entry.getValue();

            // 낙관적 락을 사용합니다.
            var stockEntity = stockRepository.findById(productId)
                    .orElseThrow(() -> new StockNotFoundException(productId));

            if (stockEntity == null) {
                throw new StockNotFoundException(productId);
            }

            if (stockEntity.getQuantity() < quantity) {
                throw new SoldOutException();
            }

            stockEntity.decreaseQuantity(quantity);
            //stockRepository.save(stockEntity);
        }
    }




}
