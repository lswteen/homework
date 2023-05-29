package kr.co._29cm.homework.domain.repository;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
