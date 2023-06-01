package kr.co._29cm.homework.domain.repository;

import kr.co._29cm.homework.domain.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockEntity, Long> {
}
