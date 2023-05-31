package kr.co._29cm.homework;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co._29cm.homework.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OptimisticLockingTest {
    private final ProductRepository productRepository;
    private final ProductService productService;

    public OptimisticLockingTest(@Autowired ProductRepository productRepository,
                                 @Autowired ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Test
    @Transactional
    public void testOptimisticLocking() {
        // 상품 생성
        ProductEntity product = ProductEntity.builder()
                .productId(648418L)
                .name("BS 02-2A DAYPACK 26 (BLACK)")
                .price(238000D)
                .quantity(5)
                .build();
        productRepository.save(product);

        // 동시에 주문 생성 시도
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        Runnable orderCreationTask = () -> {
            try {
                // 동시에 주문 생성 시도
                productService.decreaseProductQuantity(
                        Collections.singletonMap(648418L, 1), // 상품 ID: 648418L, 수량: 1
                        "user1"
                );
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        };

        // 2개의 스레드로 주문 생성 시도
        executorService.submit(orderCreationTask);
        executorService.submit(orderCreationTask);

        try {
            // 주문 생성 작업 완료 대기
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 상품 조회
        ProductEntity updatedProduct = productRepository.findById(648418L)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: 648418L"));

        // 낙관적 락 테스트
        assertEquals(0, updatedProduct.getVersion());  // 버전은 0이어야 함
        assertEquals(4, updatedProduct.getQuantity()); // 낙관적 락으로 인해 재고는 1로 차감되어야 함
    }

}