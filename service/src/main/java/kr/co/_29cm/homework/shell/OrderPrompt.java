package kr.co._29cm.homework.shell;

import kr.co._29cm.homework.shell.request.Order;
import kr.co._29cm.homework.shell.service.OrderAppService;
import kr.co._29cm.homework.shell.service.ProductAppService;
import kr.co_29cm.homework.exception.ProductNotFoundException;
import kr.co_29cm.homework.exception.SoldOutException;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;


@Component
public class OrderPrompt implements CommandLineRunner {
    private static final double DELIVERY_FEE = 2500; // 배송비 설정

    private final ProductAppService productAppService;
    private final OrderAppService orderAppService;

    public OrderPrompt(ProductAppService productAppService, OrderAppService orderAppService) {
        this.productAppService = productAppService;
        this.orderAppService = orderAppService;
    }

    @Override
    public void run(String... args) throws Exception {
        var terminal = TerminalBuilder.terminal();
        var lineReader = LineReaderBuilder
                .builder()
                .terminal(terminal)
                .build();

        while (true) {
            String line = null;
            try {
                line = lineReader.readLine(OrderPromptStrings.PROMPT_MESSAGE);
                manageOrderProcess(line, lineReader);
            } catch (UserInterruptException | EndOfFileException e) {
                return;
            }
        }
    }

    private void manageOrderProcess(String input, LineReader lineReader) {
        if ("q".equalsIgnoreCase(input)) {
            System.out.printf(OrderPromptStrings.EXIT_MESSAGE);
            System.exit(0);
        } else if ("o".equalsIgnoreCase(input)) {
            printProductInfo();
            handleOrderInput(lineReader);
            printOrderSummary();
            orderAppService.clearOrders();
        } else if("m".equalsIgnoreCase(input)){
            printOrderList();
        } else {
            System.out.printf(OrderPromptStrings.INPUT_ERROR_MESSAGE);
        }
    }

    private String removeDecimalZero(Double value) {
        var formattedValue = String.format("%.1f", value);
        return formattedValue.endsWith(".0") ? formattedValue.replace(".0", "") : formattedValue;
    }

    /**
     * 상품 정보 조회
     */
    private void printProductInfo() {
        var productList = productAppService.findByProducts();
        System.out.printf("%-10s %-60s %-12s %-10s%n",
                OrderPromptStrings.PRODUCT_NUMBER_LABEL, OrderPromptStrings.PRODUCT_NAME_LABEL,
                OrderPromptStrings.SELLING_PRICE_LABEL, OrderPromptStrings.STOCK_QUANTITY_LABEL);

        productList.stream().forEach(
                product -> System.out.printf("%-10s %-60s %-12s %-10s%n",
                    product.getProductId(), product.getName(),
                    removeDecimalZero(product.getPrice()), product.getQuantity()
                )
        );
    }

    /**
     * 주문 처리
     * @param lineReader
     */
    private void handleOrderInput(LineReader lineReader) {
        while (true) {
            var productId = lineReader.readLine(OrderPromptStrings.ORDER_PROMPT).trim();
            var quantityStr = lineReader.readLine(OrderPromptStrings.QUANTITY_PROMPT).trim();
            var userId = String.valueOf(Thread.currentThread().getId());

            // 공백 입력시 주문 완료 결제로 판단
            if (productId.isEmpty() || quantityStr.isEmpty()) {
                // 재고차감과 주문 테이블 등록을 동시에 한 트랜잭션에서 처리
                Map<Long, Integer> productQuantities = orderAppService.getOrders().stream()
                        .collect(Collectors.toMap(
                                order -> order.getProduct().getProductId(),
                                Order::getQuantity
                        ));
                orderAppService.createOrdersAndDecreaseProductQuantity(productQuantities,userId);
                break;
            }

            try {
                var quantity = Integer.parseInt(quantityStr); // 수량
                var product = productAppService.findByProductId(Long.valueOf(productId)); //상품검색
                var totalQuantity = quantity + orderAppService.getTotalQuantityForProduct(Long.valueOf(productId)); //총수량 (수량 + 현재 주문수량)
                if (product.getQuantity() < totalQuantity) { //상품에 재고수량 < 현재 총수량
                    throw new SoldOutException();
                }
                var order = new Order(product, quantity,userId);   //주문
                orderAppService.addOrder(order);
            } catch (ProductNotFoundException | SoldOutException e) {
                System.out.println(e.getMessage());
                continue;
            }
        }
    }

    /**
     * 등록된 주문 목록 계산
     */
    private void printOrderSummary() {
        System.out.println(OrderPromptStrings.ORDER_HISTORY_HEADER);
        System.out.println(OrderPromptStrings.ORDER_HISTORY_DELIMITER);
        orderAppService.getOrders().stream()
                .forEach(order -> System.out.printf("%s - %d개%n", order.getProduct().getName(), order.getQuantity()));
        System.out.println(OrderPromptStrings.ORDER_HISTORY_DELIMITER);
        var totalOrderPrice = orderAppService.getOrders().stream()
                .mapToDouble(order -> order.getProduct().getPrice() * order.getQuantity())
                .sum();
        System.out.printf("%s %,.0f원%n", OrderPromptStrings.ORDER_AMOUNT_LABEL, totalOrderPrice);
        System.out.println(OrderPromptStrings.ORDER_HISTORY_DELIMITER);
        if (totalOrderPrice < 50000 && totalOrderPrice > 0) {
            totalOrderPrice += DELIVERY_FEE;
        }
        System.out.printf("%s %,.0f원%n", OrderPromptStrings.PAYMENT_AMOUNT_LABEL, totalOrderPrice);
        System.out.println(OrderPromptStrings.ORDER_HISTORY_DELIMITER);
    }

    private void printOrderList() {
        var orders = orderAppService.getH2OrderList();
        if (orders.isEmpty()) {
            System.out.println("No orders placed yet.");
            return;
        }

        System.out.println("Order List:");
        for (Order order : orders) {
            System.out.println("Product ID: " + order.getProduct().getProductId());
            System.out.println("Quantity: " + order.getQuantity());
            System.out.println("User ID: " + order.getUserId());
            System.out.println("-----------------------------");
        }
    }
}