package kr.co._29cm.homework.system;

import kr.co._29cm.homework.domain.service.ProductService;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OrderSystem implements CommandLineRunner {

    private final ProductService productService;

    public OrderSystem(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        Terminal terminal = TerminalBuilder.terminal();
        LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        String prompt = "입력(o[order]: 주문, q[quit]: 종료) : ";

        while (true) {
            String line = null;
            try {
                line = lineReader.readLine(prompt);
                handleInput(line);
            } catch (UserInterruptException | EndOfFileException e) {
                return;
            }
        }
    }

    private void handleInput(String input) {
        if ("q".equalsIgnoreCase(input)) {
            System.out.println("시스템 종료");
            System.exit(0);
        } else if ("o".equalsIgnoreCase(input)) {
            System.out.println("상품 정보:");
            var productList = productService.findByProducts();
            productList.forEach(productEntity ->{
                System.out.println("상품번호: " + productEntity.getId() + ", 상품명: " + productEntity.getName() + ", 판매가격: " + productEntity.getPrice() + ", 재고수량: " + productEntity.getStock());
            });
        } else {
            System.out.println("알 수 없는 명령어");
        }
    }
}
