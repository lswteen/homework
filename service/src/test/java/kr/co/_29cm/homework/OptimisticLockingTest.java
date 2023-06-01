package kr.co._29cm.homework;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co._29cm.homework.domain.repository.StockRepository;
import kr.co._29cm.homework.domain.service.ProductService;
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
    private final ProductService productService;
    private final StockService stockService;
    private final StockRepository stockRepository;

    public OptimisticLockingTest(@Autowired ProductRepository productRepository,
                                 @Autowired ProductService productService,
                                 @Autowired StockService stockService,
                                 @Autowired StockRepository stockRepository) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.stockService = stockService;
        this.stockRepository = stockRepository;
    }

    private static final int INITIAL_QUANTITY = 10;

    private ProductEntity createProductEntity() {
        // 상품 생성
        ProductEntity productEntity = new ProductEntity(10800L,"BS 02-2A DAYPACK 26 (BLACK)",238000D,INITIAL_QUANTITY);
        productRepository.saveAndFlush(productEntity);
        return productEntity;
    }

    @Test
    void 멀티스레드_재고_소진() throws ExecutionException, InterruptedException {
        ProductEntity productEntity = createProductEntity();

        var numberOfThreads = 10;
        var executorService = Executors.newFixedThreadPool(numberOfThreads);
        var failedCount = new AtomicInteger(0);
        var soldOutCount = new AtomicInteger(0);
        List<Future<?>> tasks = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            Future<?> task = executorService.submit(() -> {
                try {
                    stockService.decreaseStock(Collections.singletonMap(productEntity.getProductId(), 3));
                }catch (SoldOutException e) {
                    soldOutCount.incrementAndGet();
                }catch (ObjectOptimisticLockingFailureException e) {
                    failedCount.incrementAndGet();
                }
            });
            tasks.add(task);
        }
        for (Future<?> task : tasks) {
            task.get(); // Wait for all tasks to complete
        }
        System.out.println("ObjectOptimisticLockingFailureException count: " + failedCount.get());
        System.out.println("SoldOutException count: " + soldOutCount.get());
        assertThat(soldOutCount.get()).isEqualTo(7);

    }


}