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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class OrderPrompt implements CommandLineRunner {

    private final ProductAppService productAppService;
    private final OrderAppService orderAppService;
    private final Map<String, Consumer<LineReader>> actions;

    public OrderPrompt(ProductAppService productAppService, OrderAppService orderAppService) {
        this.productAppService = productAppService;
        this.orderAppService = orderAppService;
        this.actions = new HashMap<>();
        initActions();
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

    private void initActions() {
        actions.put("q", this::exitAction);
        actions.put("o", this::orderAction);
        actions.put("default", this::errorAction);
    }

    private void exitAction(LineReader lineReader) {
        System.out.printf(OrderPromptStrings.EXIT_MESSAGE);
        System.exit(0);
    }

    private void errorAction(LineReader lineReader) {
        System.out.printf(OrderPromptStrings.INPUT_ERROR_MESSAGE);
    }

    private void orderAction(LineReader lineReader) {
        productAppService.printProductInfo();
        orderAppService.handleOrderInput(lineReader);
        orderAppService.printOrderSummary();
        orderAppService.clearOrders();
    }

    private void manageOrderProcess(String input, LineReader lineReader) {
        Consumer<LineReader> action = actions.getOrDefault(input, actions.get("default"));
        action.accept(lineReader);
    }
}