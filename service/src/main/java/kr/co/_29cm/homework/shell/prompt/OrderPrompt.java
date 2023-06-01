package kr.co._29cm.homework.shell.prompt;

import kr.co._29cm.homework.shell.request.Order;
import kr.co._29cm.homework.shell.service.OrderAppService;
import kr.co._29cm.homework.shell.service.ProductAppService;
import kr.co_29cm.homework.exception.ProductNotFoundException;
import kr.co_29cm.homework.exception.SoldOutException;
import lombok.RequiredArgsConstructor;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static kr.co._29cm.homework.shell.prompt.OrderPromptSupport.*;

@Component
@RequiredArgsConstructor
public class OrderPrompt implements CommandLineRunner {

    private final ProductAppService productAppService;
    private final OrderAppService orderAppService;

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
            exitAction();
        } else if ("o".equalsIgnoreCase(input)) {
            printProductInfo();
            handleOrderInput(lineReader);
            printOrderSummary();
            orderAppService.clearOrders();
        } else {
            errorAction();
        }
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
                    removeDecimalZero(product.getPrice()), product.getStock().getQuantity()
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

            if (payment(productId, quantityStr)) break;

            try {
                var quantity = Integer.parseInt(quantityStr);
                var totalQuantity = quantity + orderAppService.getTotalQuantityForProduct(Long.valueOf(productId));
                var product = productAppService.findByProductId(Long.valueOf(productId));
                if (product.getStock().getQuantity() < totalQuantity) { // 상품에 재고수량 < 현재 총수량
                    throw new SoldOutException();
                }
                var order = new Order(product, quantity, userId);
                orderAppService.addOrder(order);
            } catch (ProductNotFoundException | SoldOutException e) {
                System.out.println(e.getMessage());
                continue;
            }
        }
    }

    /**
     * 결제시 재고 차감 ROCK LockModeType.PESSIMISTIC_WRITE
     * @param productId
     * @param quantityStr
     * @return
     */
    private boolean payment(String productId, String quantityStr) {
        if (productId.isEmpty() || quantityStr.isEmpty()) {
            var productQuantities = orderAppService.getOrders().stream()
                    .collect(Collectors.toMap(
                            order -> order.getProduct().getProductId(),
                            Order::getQuantity
                    ));
            productAppService.decreaseProductQuantity(productQuantities);
            return true;
        }
        return false;
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
        totalOrderPrice = deliveryCharge(totalOrderPrice);
        System.out.printf("%s %,.0f원%n", OrderPromptStrings.PAYMENT_AMOUNT_LABEL, totalOrderPrice);
        System.out.println(OrderPromptStrings.ORDER_HISTORY_DELIMITER);
    }

}