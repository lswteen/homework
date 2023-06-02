package kr.co._29cm.homework.shell.prompt;

import kr.co._29cm.homework.shell.service.OrderAppService;
import kr.co._29cm.homework.shell.service.ProductAppService;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class OrderPrompt implements CommandLineRunner {

    private final ProductAppService productAppService;
    private final OrderAppService orderAppService;
    private final Map<String, OrderAction> actionMap;

    public OrderPrompt(ProductAppService productAppService, OrderAppService orderAppService) {
        this.productAppService = productAppService;
        this.orderAppService = orderAppService;
        this.actionMap = Map.of(
                "q", this::exitAction,
                "o", this::orderAction,
                "default", this::errorAction
        );
    }
    private void exitAction(LineReader lineReader) {
        System.out.printf(OrderPromptStrings.EXIT_MESSAGE);
        System.exit(0);
    }

    private void orderAction(LineReader lineReader) {
        productAppService.printProductInfo();
        orderAppService.handleOrderInput(lineReader);
        orderAppService.printOrderSummary();
        orderAppService.clearOrders();
    }

    private void errorAction(LineReader lineReader) {
        System.out.printf(OrderPromptStrings.INPUT_ERROR_MESSAGE);
    }

    @Override
    public void run(String... args) throws Exception {
        final var lineReader = LineReaderBuilder
                .builder()
                .terminal(TerminalBuilder.terminal())
                .build();

        while (true) {
            try {
                Optional.ofNullable(lineReader.readLine(OrderPromptStrings.PROMPT_MESSAGE))
                        .map(String::toLowerCase)
                        .map(this::lineToAction)
                        .ifPresent(goToOrderAction(lineReader));
            } catch (UserInterruptException | EndOfFileException e) {
                return;
            }
        }
    }

    private Consumer<OrderAction> goToOrderAction(LineReader lineReader) {
        return action -> action.execute(lineReader);
    }

    private OrderAction lineToAction(String line) {
        return actionMap.getOrDefault(line, actionMap.get("default"));
    }

}