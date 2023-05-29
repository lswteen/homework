package kr.co._29cm.homework.domain.repository;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
