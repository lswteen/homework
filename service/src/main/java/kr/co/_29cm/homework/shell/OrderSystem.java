package kr.co._29cm.homework.shell;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.domain.service.ProductService;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderSystem  {
//    private static final String ORDER_PROMPT = "상품번호 : ";
//    private static final String QUANTITY_PROMPT = "수량 : ";
//    private static final double DELIVERY_FEE = 2500; // 배송비 설정
//
//    private final ProductService productService;
//
//    public OrderSystem(ProductService productService) {
//        this.productService = productService;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        var terminal = TerminalBuilder.terminal();
//        var lineReader = LineReaderBuilder.builder().terminal(terminal).build();
//        var prompt = "입력(o[order]: 주문, q[quit]: 종료) : ";
//
//        while (true) {
//            String line = null;
//            try {
//                line = lineReader.readLine(prompt);
//                handleInput(line, lineReader);
//            } catch (UserInterruptException | EndOfFileException e) {
//                return;
//            }
//        }
//    }
//
//    private void handleInput(String input, LineReader lineReader) {
//        if ("q".equalsIgnoreCase(input)) {
//            System.out.println("시스템 종료");
//            System.exit(0);
//        } else if ("o".equalsIgnoreCase(input)) {
//            System.out.println("상품 정보:");
//            var productList = productService.findByProducts();
//            System.out.printf("%-10s %-60s %-12s %-10s%n", "상품번호", "상품명", "판매가격", "재고수량");
//            for (ProductEntity productEntity : productList) {
//                System.out.printf("%-10s %-60s %-12s %-10s%n",
//                        productEntity.getProductId(), productEntity.getName(),
//                        productEntity.getPrice(), productEntity.getStock());
//            }
//
//            Map<ProductEntity, Integer> orders = new HashMap<>();
//            double totalOrderPrice = 0.0;
//
//            while (true) {
//                // 주문 입력 처리
//                var productId = lineReader.readLine(ORDER_PROMPT).trim();
//                var quantityStr = lineReader.readLine(QUANTITY_PROMPT).trim();
//
//                // 공백 입력시 주문 종료
//                if (productId.isEmpty() || quantityStr.isEmpty()) {
//                    break;
//                }
//
//                var quantity = Integer.parseInt(quantityStr);
//                var productEntity = productService.findProductById(Long.valueOf(productId));
//
//                if(productEntity == null) {
//                    System.out.println("해당 상품이 없습니다.");
//                    continue;
//                }
//
//                if(productEntity.getStock() < quantity) {
//                    System.out.println("재고가 부족합니다.");
//                    continue;
//                }
//                // 이미 주문한 상품인지 확인하고, 주문 수량 합산
//                if (orders.containsKey(productEntity)) {
//                    orders.put(productEntity, orders.get(productEntity) + quantity);
//                } else {
//                    orders.put(productEntity, quantity);
//                }
//
//                double orderPrice = productEntity.getPrice() * quantity;
//                totalOrderPrice += orderPrice;
//
//                // 재고 감소 및 주문 정보 업데이트 로직 구현 필요
//            }
//
//            double totalPaymentPrice = totalOrderPrice <= 50000 ? totalOrderPrice + DELIVERY_FEE : totalOrderPrice;
//
//            System.out.println("----------------------");
//            for(Map.Entry<ProductEntity, Integer> entry : orders.entrySet()) {
//                System.out.printf("%s - %d개\n", entry.getKey().getName(), entry.getValue());
//            }
//            System.out.println("----------------------");
//            System.out.printf("주문금액 : %.2f\n", totalOrderPrice);
//            System.out.println("----------------------");
//            System.out.printf("지불금액 : %.2f 원\n", totalPaymentPrice);
//
//        } else {
//            System.out.println("알 수 없는 명령어");
//        }
//    }


}
