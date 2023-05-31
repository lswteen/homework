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
public class OrderAppService {
    private Map<Long, Order> orders;

    public OrderAppService() {
        this.orders = new HashMap<>();
    }

    public int getTotalQuantityForProduct(Long productId) {
        return orders.values().stream()
                .filter(order -> order.getProduct().getProductId().equals(productId))
                .mapToInt(Order::getQuantity)
                .sum();
    }

    //Order객체에 대한 불변성유지를 위해서 수량 변경이 이뤄지면 새로 new로 만들어서 집어넣는다.
    public void addOrder(Order order) {
        var productId = order.getProduct().getProductId();
        if (orders.containsKey(productId)) {
            var existingOrder = orders.get(productId);
            var newQuantity = existingOrder.getQuantity() + order.getQuantity();
            var newOrder = new Order(order.getProduct(), newQuantity,order.getUserId());
            orders.put(productId, newOrder);
        } else {
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
