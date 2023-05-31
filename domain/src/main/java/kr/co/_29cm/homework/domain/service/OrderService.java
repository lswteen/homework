package kr.co._29cm.homework.domain.service;

import kr.co._29cm.homework.domain.entity.OrderEntity;
import kr.co._29cm.homework.domain.repository.OrderRepository;
import kr.co._29cm.homework.domain.repository.ProductRepository;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    @Transactional(readOnly = true)
    public List<OrderEntity> findByOrders(){
        return orderRepository.findAll();
    }

    @Transactional
    public List<OrderEntity> createOrdersAndDecreaseProductQuantity(Map<Long, Integer> productQuantities, String userId) {
        List<OrderEntity> orders = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            var productId = entry.getKey();
            var quantity = entry.getValue();

            var product = productRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));

            if (product.getQuantity() < quantity) {
                throw new SoldOutException();
            }

            OrderEntity order = new OrderEntity(product, quantity, userId);
            orders.add(orderRepository.saveAndFlush(order));
        }

        return orders;
    }
}
