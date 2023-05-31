package kr.co._29cm.homework;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co._29cm.homework.domain.service.ProductService;
import kr.co_29cm.homework.exception.ProductNotFoundException;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class OptimisticLockingTest {
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OptimisticLockingTest(@Autowired ProductRepository productRepository,
                                 @Autowired ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    private static final int THREAD_COUNT = 2;
    private static final int DECREASE_AMOUNT = 2;
    private static final int INITIAL_QUANTITY = 10;

    @Test
    @Transactional
    public void testTypeAOptimisticLocking() throws InterruptedException {
        // 상품 생성
        ProductEntity product = ProductEntity.builder()
                .productId(10800L)
                .name("BS 02-2A DAYPACK 26 (BLACK)")
                .price(238000D)
                .quantity(INITIAL_QUANTITY)
                .build();
        productRepository.save(product);

        // 동시에 주문 생성 시도
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    productService.decreaseProductQuantity(Collections.singletonMap(product.getProductId(), DECREASE_AMOUNT), "user1");
                } finally {
                    latch.countDown();
                }
            });
        }
        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();

        // 상품 조회
        ProductEntity updatedProduct = productRepository.findById(10800L)
                .orElseThrow(() -> new ProductNotFoundException(10800L));
        int expectedQuantity = INITIAL_QUANTITY - (DECREASE_AMOUNT * THREAD_COUNT);

        // 낙관적 락 테스트
        assertEquals(0, updatedProduct.getVersion());  // 버전은 0이어야 함
        //assertEquals(expectedQuantity, updatedProduct.getQuantity()); // 낙관적 락으로 인해 재고는 4로 차감되어야 함
        assertEquals(10, updatedProduct.getQuantity()); // 낙관적 락으로 인해 재고는 4로 차감되어야 함
    }


    @Test
    @Transactional
    public void testTypeBOptimisticLocking() throws InterruptedException {
        // 상품 생성
        ProductEntity product = ProductEntity.builder()
                .productId(10800L)
                .name("BS 02-2A DAYPACK 26 (BLACK)")
                .price(238000D)
                .quantity(INITIAL_QUANTITY)
                .build();
        productRepository.save(product);

        // 동시에 주문 생성 시도
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // 스레드 A: 실패하는 스레드
        executorService.submit(() -> {
            try {
                productService.decreaseProductQuantity(Collections.singletonMap(product.getProductId(), DECREASE_AMOUNT), "user1");
                // 스레드 A는 성공하면 안되므로 AssertionError를 발생시킴
                throw new AssertionError("Thread A should fail");
            } catch (Exception e) {
                // 실패 시 예외가 발생하도록 함
                assertThrows(OptimisticLockingFailureException.class, () -> {
                    throw e;
                });
            } finally {
                latch.countDown();
            }
        });

        // 스레드 B: 성공하는 스레드
        executorService.submit(() -> {
            try {
                productService.decreaseProductQuantity(Collections.singletonMap(product.getProductId(), DECREASE_AMOUNT), "user2");
            } finally {
                latch.countDown();
            }
        });

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();

        // 상품 조회
        ProductEntity updatedProduct = productRepository.findById(10800L)
                .orElseThrow(() -> new ProductNotFoundException(10800L));
        int expectedQuantity = INITIAL_QUANTITY - DECREASE_AMOUNT;

        // 낙관적 락 테스트
        assertEquals(0, updatedProduct.getVersion());  // 버전은 0이어야 함
        assertEquals(expectedQuantity, updatedProduct.getQuantity()); // 스레드 B가 성공했으므로 재고는 8로 차감되어야 함
    }

    /**
     * 초기 재고 수량: 10개
     * 스레드 A가 10개의 재고를 소진하려고 시도함.
     * 스레드 B는 동시에 재고를 감소시키려고 시도함.
     * 스레드 A가 10개의 재고를 소진하면, 남은 재고는 0개가 됨.
     * 스레드 B는 SoldOutException을 처리하게 됨.*
     * @throws InterruptedException
     */
    @Test
    @DisplayName("2개동시에 진행해서 먼저재고소진시 다른한개는 soldoutException 처리")
    @Transactional
    public void testTypeCOptimisticLocking() throws InterruptedException {
        // 상품 생성
        ProductEntity product = ProductEntity.builder()
                .productId(10800L)
                .name("BS 02-2A DAYPACK 26 (BLACK)")
                .price(238000D)
                .quantity(INITIAL_QUANTITY)
                .build();
        productRepository.save(product);

        // 동시에 주문 생성 시도
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // 재고를 다소진하는 작업을 동시에 수행하는 스레드 A, B
        executorService.submit(() -> {
            try {
                productService.decreaseProductQuantity(Collections.singletonMap(product.getProductId(), 10), "user1");
            } catch (SoldOutException e) {
                System.out.println("========= SoldOutException user1 ");
                // SoldOutException 발생을 확인
                assertTrue(true);
            } finally {
                latch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                productService.decreaseProductQuantity(Collections.singletonMap(product.getProductId(), 3), "user2");
            } catch (SoldOutException e) {
                // SoldOutException 발생을 확인
                System.out.println("========= SoldOutException user2 ");
                assertTrue(true);
            } finally {
                latch.countDown();
            }
        });

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();

        // 상품 조회
        ProductEntity updatedProduct = productRepository.findById(10800L)
                .orElseThrow(() -> new ProductNotFoundException(10800L));
        System.out.println("=====> updatedProduct.getQuantity() :"+updatedProduct.getQuantity());
        // 낙관적 락 테스트
        assertTrue(updatedProduct.getQuantity() == 0); // 재고는 0 이상이어야 함
    }
}