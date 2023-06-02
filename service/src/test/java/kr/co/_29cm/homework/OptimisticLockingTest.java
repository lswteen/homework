package kr.co._29cm.homework;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.entity.StockEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co._29cm.homework.domain.repository.StockRepository;
import kr.co._29cm.homework.domain.service.StockService;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class OptimisticLockingTest {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;

    public OptimisticLockingTest(@Autowired ProductRepository productRepository,
                                 @Autowired StockRepository stockRepository,
                                 @Autowired StockService stockService) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
    }

    private static final int INITIAL_QUANTITY = 10;

    private ProductEntity createProductEntity() {
        ProductEntity productEntity = new ProductEntity(
            10800L,
            "BS 02-2A DAYPACK 26 (BLACK)",
            238000D,
            null
        );
        productRepository.save(productEntity);

        StockEntity stockEntity = new StockEntity(10800L, INITIAL_QUANTITY);
        stockRepository.save(stockEntity);

        productEntity = new ProductEntity(
                10800L,
                "BS 02-2A DAYPACK 26 (BLACK)",
                238000D,
                stockEntity
        );
        return productEntity;
    }

    @Test
    void multithread_throws_OptimisticLockException_when_productinventory_exhaustion() throws ExecutionException, InterruptedException {
        ProductEntity productEntity = createProductEntity();

        var numberOfThreads = 10;
        var executorService = Executors.newFixedThreadPool(numberOfThreads);
        var optimisticLockCount = new AtomicInteger(0);
        var soldOutCount = new AtomicInteger(0);
        List<Future<?>> tasks = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            Future<?> task = executorService.submit(() -> {
                try {
                    stockService.objectOptimisticLockingDecreaseStock(Collections.singletonMap(productEntity.getProductId(), 3));
                } catch (ObjectOptimisticLockingFailureException e) {
                    optimisticLockCount.incrementAndGet();
                } catch(SoldOutException e){
                    soldOutCount.incrementAndGet();
                }

            });
            tasks.add(task);
        }
        for (Future<?> task : tasks) {
            task.get();
        }

        assertThat(optimisticLockCount.get()).isEqualTo(0);
        assertThat(soldOutCount.get()).isEqualTo(7);
    }


}