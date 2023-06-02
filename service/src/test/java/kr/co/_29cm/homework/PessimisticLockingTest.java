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
public class PessimisticLockingTest {
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StockService stockService;

    public PessimisticLockingTest(@Autowired ProductRepository productRepository,
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

    /**
     * 10 개의 스레드를 생성하고
     * 미리 Mock으로 등록된 상품의 재고 10개를 차감하는 로직입니다.
     *
     * 한번에 3개의 상품재고를 소진하기때문에 동시에 10개를 진행하면 3개만성공하고
     * 7번의 SoldOutException 이 발생하고 filedCount 를 증가 시키게 됩니다.
     *
     * 요구사항에서 멀티스레드 동시 재고차감에서 SoldOutException 이 목적이기에 해당 Rock을
     * LockModeType.PESSIMISTIC_WRITE 으로 변경 하였습니다.
     *
     * 낙관적 락으로 처리할수있는 재고도 고민해보고
     * branch -> feature/object-optmistic-locking
     * retry, optmistic 이용한 재고차감 기능 및 테스트 코드까지 성공한 케이스 추가 하였습니다.
     *
     * 처음 테이블구조는 product 에 상품번호, 상품명, 가격, 상품재고 같이 있는 구조였지만
     * LockModeType.PESSIMISTIC_WRITE 으로 인하여 상품 테이블 트랜잭션락으로 인하여
     *
     * 추후 데이터가 증가하게되면 상품조회 와 재고 변경 이라는 Read/Write
     * 사용빈도가 다른 2개의 테이블을 용도에 맞게 분리 하였습니다.
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    void multithread_throws_SoldOutException_when_productinventory_exhaustion() throws ExecutionException, InterruptedException {
        ProductEntity productEntity = createProductEntity();

        var numberOfThreads = 10;
        var executorService = Executors.newFixedThreadPool(numberOfThreads);
        var soldOutCount = new AtomicInteger(0);

        List<Future<?>> tasks = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            Future<?> task = executorService.submit(() -> {
                try {
                    stockService.decreaseStock(Collections.singletonMap(productEntity.getProductId(), 3));
                }catch (SoldOutException e) {
                    soldOutCount.incrementAndGet();
                }
            });
            tasks.add(task);
        }
        for (Future<?> task : tasks) {
            task.get();
        }
        System.out.println("SoldOutException count: " + soldOutCount.get());
        assertThat(soldOutCount.get()).isEqualTo(7);

    }


}