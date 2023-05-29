package kr.co._29cm.homework.shell.service;

import kr.co._29cm.homework.shell.request.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderService {
    private Map<Long, Order> orders;

    public OrderService() {
        this.orders = new HashMap<>();
    }

    public void addOrder(Order order) {
        var productId = order.getProduct().getProductId();

        if (orders.containsKey(productId)) {
            var existingOrder = orders.get(productId);
            existingOrder.addQuantity(order.getQuantity());
        } else {
            log.info("productId :{}, order : {} ", productId, order.toString());
            orders.put(productId, order);
        }
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders.values());
    }

    public void clearOrders() {
        orders.clear();
    }
}
