package kr.co._29cm.homework.shell.service;

import jakarta.annotation.PreDestroy;
import kr.co._29cm.homework.shell.request.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
public class OrderAppService {
    private ConcurrentMap<Long, Order> orders;
    private ExecutorService executorService;

    public OrderAppService() {
        this.orders = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    @PreDestroy
    public void cleanup() {
        executorService.shutdown();
    }

    public int getTotalQuantityForProduct(Long productId) {
        return orders.values().stream()
                .filter(order -> order.getProduct().getProductId().equals(productId))
                .mapToInt(Order::getQuantity)
                .sum();
    }

    public Future<Void> addOrder(Order order) {
        return executorService.submit(() -> {
            var productId = order.getProduct().getProductId();
            orders.compute(productId, (key, existingOrder) -> {
                if (existingOrder == null) {
                    return order;
                } else {
                    // Assuming Order is immutable, create a new Order with the added quantity
                    int newQuantity = existingOrder.getQuantity() + order.getQuantity();
                    return new Order(existingOrder.getProduct(), newQuantity);
                }
            });
            return null;
        });
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders.values());
    }

    public Future<Void> clearOrders() {
        return executorService.submit(() -> {
            orders.clear();
            return null;
        });
    }
}
