package kr.co._29cm.homework.shell;

import kr.co._29cm.homework.shell.request.Order;
import kr.co._29cm.homework.shell.request.Product;
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

@Component
public class OrderPrompt implements CommandLineRunner {
    private static final String ORDER_PROMPT = "상품번호 : ";
    private static final String QUANTITY_PROMPT = "수량 : ";
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
        var prompt = "입력(o[order]: 주문, q[quit]: 종료) : ";

        while (true) {
            String line = null;
            try {
                line = lineReader.readLine(prompt);
                manageOrderProcess(line, lineReader);
            } catch (UserInterruptException | EndOfFileException e) {
                return;
            }
        }
    }

    private void manageOrderProcess(String input, LineReader lineReader) {
        if ("q".equalsIgnoreCase(input)) {
            System.out.printf("시스템 종료");
            System.exit(0);
        } else if ("o".equalsIgnoreCase(input)) {
            printProductInfo();             //상품 리스트 뿌리고 상품번호,재고수량으로 넘김
            handleOrderInput(lineReader);   //공백제거하고 empty로 들어오면 order list계산 하고 결제한다는 가정
            //여기서 재고 체크하고 재고가 없으면 실패 메시지로 떨궈버림 Exception 처리
            //재고 있으면 재고 차감 처리
            printOrderSummary();            //재고 정상이라면 주문금액 처리
            orderAppService.clearOrders();     //주문 완료 시 Map 자료구조 초기화 (해당 쓰레드만)
        } else {
            System.out.printf("입력값 오류");
        }
    }

    private void printProductInfo() {
        var productList = productAppService.findByProducts();
        System.out.printf("%-10s %-60s %-12s %-10s%n", "상품번호", "상품명", "판매가격", "재고수량");

        productList.stream().forEach(
            product -> System.out.printf("%-10s %-60s %-12s %-10s%n",
                    product.getProductId(), product.getName(),
                    product.getPrice(), product.getQuantity()
            )
        );
    }

    private void handleOrderInput(LineReader lineReader) {
        while (true) {
            var productId = lineReader.readLine(ORDER_PROMPT).trim();
            var quantityStr = lineReader.readLine(QUANTITY_PROMPT).trim();

            // 공백 입력시 주문 완료 결제로 판단
            if (productId.isEmpty() || quantityStr.isEmpty()) {
                // 주문에 대한 최종 결제완료 판단으로 재고를 차감시킴.

                break;
            }

            var quantity = Integer.parseInt(quantityStr);

            Product product = null;
            try {
                // 상품 조회
                product = productAppService.findByProductId(Long.valueOf(productId));

                //재고 수량
                int totalQuantity = quantity + orderAppService.getTotalQuantityForProduct(Long.valueOf(productId));
                if (product.getQuantity() < totalQuantity) {
                    throw new SoldOutException();
                }

                //주문 추가
                Order order = new Order(product, quantity);
                orderAppService.addOrder(order);
            } catch (ProductNotFoundException | SoldOutException e) {
                System.out.println(e.getMessage());
                continue;
            }
        }
    }

    private void printOrderSummary() {
        // 주문 내역 출력
        System.out.println("주문 내역:");
        System.out.println("------------------------------------------------------");
        orderAppService.getOrders().stream()
                .forEach(order -> System.out.printf("%s - %d개%n", order.getProduct().getName(), order.getQuantity()));
        System.out.println("------------------------------------------------------");
        //주문 금액 계산
        var totalOrderPrice = orderAppService.getOrders().stream()
                .mapToDouble(order -> order.getProduct().getPrice() * order.getQuantity())
                .sum();
        System.out.printf("주문 금액: %.2f원%n", totalOrderPrice);
        System.out.println("------------------------------------------------------");
        // 배송비 추가
        if (totalOrderPrice < 50000 && totalOrderPrice > 0) {
            totalOrderPrice += DELIVERY_FEE;
        }
        System.out.printf("지불 금액: %.2f원%n", totalOrderPrice);
        System.out.println("------------------------------------------------------");
    }


}
