package kr.co._29cm.homework.shell;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.service.ProductService;
import kr.co._29cm.homework.shell.request.Order;
import kr.co._29cm.homework.shell.service.OrderService;
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

    private final ProductService productService;
    private final OrderService orderService;


    public OrderPrompt(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) throws Exception {
        var terminal = TerminalBuilder.terminal();
        var lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        var prompt = "입력(o[order]: 주문, q[quit]: 종료) : ";

        while (true) {
            String line = null;
            try {
                line = lineReader.readLine(prompt);
                handleInput(line, lineReader);
            } catch (UserInterruptException | EndOfFileException e) {
                return;
            }
        }
    }

    private void handleInput(String input, LineReader lineReader) {
        if ("q".equalsIgnoreCase(input)) {
            System.out.println("시스템 종료");
            System.exit(0);
        } else if ("o".equalsIgnoreCase(input)) {
            printProductInfo();     //상품 리스트 뿌리고 상품번호,재고수량으로 넘김
            handleOrderInput(lineReader);   //공백제거하고 empty로 들어오면 order list계산
            //-------- 쓰레드 처리.
            //여기서 재고 체크하고 재고가 없으면 실패 메시지로 떨궈버림 Exception 처리
            //재고 있으면 재고 차감 처리
            //--------
            printOrderSummary();    //재고 정상이라면 주문금액 처리
        } else {
            System.out.println("잘못된 입력입니다.");
        }
    }


    private void printProductInfo() {
        // 상품 정보 출력하는 코드...
        System.out.println("상품 정보:");
        var productList = productService.findByProducts();
        System.out.printf("%-10s %-60s %-12s %-10s%n", "상품번호", "상품명", "판매가격", "재고수량");
        for (ProductEntity productEntity : productList) {
            System.out.printf("%-10s %-60s %-12s %-10s%n",
                    productEntity.getProductId(), productEntity.getName(),
                    productEntity.getPrice(), productEntity.getStock());
        }
    }

    private void handleOrderInput(LineReader lineReader) {
        while (true) {
            var productId = lineReader.readLine(ORDER_PROMPT).trim();
            var quantityStr = lineReader.readLine(QUANTITY_PROMPT).trim();

            // 공백 입력시 주문 종료
            if (productId.isEmpty() || quantityStr.isEmpty()) {
                // 이부분에서 재고를 확인할필요가 있음.
                break;
            }

            var quantity = Integer.parseInt(quantityStr);
            var productEntity = productService.findProductById(Long.valueOf(productId));

            if(productEntity == null) {
                System.out.println("해당 상품이 없습니다.");
                continue;
            }

            if(productEntity.getStock() < quantity) {
                System.out.println("재고가 부족합니다.");
                continue;
            }

            // 주문 추가
            Order order = new Order(productEntity, quantity);
            orderService.addOrder(order);

            //System.out.printf("%s x %d = %.2f%n", productEntity.getName(), quantity, productEntity.getPrice() * quantity);
        }
    }

    private void printOrderSummary() {
        double totalOrderPrice = 0.0;

        // 주문 내역 출력
        System.out.println("주문 내역:");
        for (Order order : orderService.getOrders()) {
            System.out.printf("%s x %d%n", order.getProduct().getName(), order.getQuantity());
            totalOrderPrice += order.getProduct().getPrice() * order.getQuantity();
        }

        // 배송비 추가
        if (totalOrderPrice < 50000 && totalOrderPrice > 0) {
            totalOrderPrice += DELIVERY_FEE;
        }

        System.out.printf("총 주문 금액: %.2f%n", totalOrderPrice);
    }


}
