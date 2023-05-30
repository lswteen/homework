package kr.co._29cm.homework.domain.service;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest(classes = {ProductRepository.class})
class ProductServiceTest {
    @MockBean
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 목록 전체 조회")
    void product_find_all(){
        //productService.findByProducts().forEach(v->System.out.println(v.toString()));
        productRepository.findAll().forEach(v->System.out.println(v.toString())  );
    }

    @Test
    @DisplayName("상품 저장")
    void product_save(){
        productRepository.save(ProductEntity.builder()
                .name("test")
                .price(1000D)
                .quantity(3)
                .build());

    }
}