package kr.co._29cm.homework.shell.service;

import kr.co._29cm.homework.shell.prompt.OrderPromptStrings;
import kr.co._29cm.homework.shell.request.Order;
import kr.co_29cm.homework.exception.ProductNotFoundException;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderAppService {
    private static final double DELIVERY_FEE = 2500; // 배송비 설정

    private final Map<Long, Order> orders;
    private final ProductAppService productAppService;

    public OrderAppService(ProductAppService productAppService) {
        this.productAppService = productAppService;
        this.orders = new HashMap<>();
    }

    public int getTotalQuantityForProduct(Long productId) {
        return orders.values().stream()
                .filter(order -> order.getProduct().getProductId().equals(productId))
                .mapToInt(Order::getQuantity)
                .sum();
    }

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

    public void handleOrderInput(LineReader lineReader) {
        while (true) {
            var productId = lineReader.readLine(OrderPromptStrings.ORDER_PROMPT).trim();
            var quantityStr = lineReader.readLine(OrderPromptStrings.QUANTITY_PROMPT).trim();
            var userId = String.valueOf(Thread.currentThread().getId());

            if (payment(productId, quantityStr)){
                break;
            }

            try {
                var quantity = Integer.parseInt(quantityStr);
                var totalQuantity = quantity + getTotalQuantityForProduct(Long.valueOf(productId));
                var product = productAppService.findByProductId(Long.valueOf(productId));
                if (product.getStock().getQuantity() < totalQuantity) { // 상품에 재고수량 < 현재 총수량
                    throw new SoldOutException();
                }
                var order = new Order(product, quantity, userId);
                addOrder(order);
            } catch (ProductNotFoundException | SoldOutException e) {
                System.out.println(e.getMessage());
                continue;
            }
        }
    }

    public void printOrderSummary() {
        System.out.println(OrderPromptStrings.ORDER_HISTORY_HEADER);
        System.out.println(OrderPromptStrings.ORDER_HISTORY_DELIMITER);
        getOrders().stream()
                .forEach(order -> System.out.printf("%s - %d개%n", order.getProduct().getName(), order.getQuantity()));
        System.out.println(OrderPromptStrings.ORDER_HISTORY_DELIMITER);
        var totalOrderPrice = getOrders().stream()
                .mapToDouble(order -> order.getProduct().getPrice() * order.getQuantity())
                .sum();
        System.out.printf("%s %,.0f원%n", OrderPromptStrings.ORDER_AMOUNT_LABEL, totalOrderPrice);
        System.out.println(OrderPromptStrings.ORDER_HISTORY_DELIMITER);
        totalOrderPrice = deliveryCharge(totalOrderPrice);
        System.out.printf("%s %,.0f원%n", OrderPromptStrings.PAYMENT_AMOUNT_LABEL, totalOrderPrice);
        System.out.println(OrderPromptStrings.ORDER_HISTORY_DELIMITER);
    }

    private boolean payment(String productId, String quantityStr) {
        if (productId.isEmpty() || quantityStr.isEmpty()) {
            var productQuantities = getOrders().stream()
                    .collect(Collectors.toMap(
                            order -> order.getProduct().getProductId(),
                            Order::getQuantity
                    ));
            productAppService.decreaseProductQuantity(productQuantities);
            return true;
        }
        return false;
    }

    private double deliveryCharge(double totalOrderPrice) {
        if (totalOrderPrice < 50000 && totalOrderPrice > 0) {
            totalOrderPrice += DELIVERY_FEE;
        }
        return totalOrderPrice;
    }
}
