package kr.co._29cm.homework.domain.repository;

import kr.co._29cm.homework.domain.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
