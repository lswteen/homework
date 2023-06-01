package kr.co._29cm.homework.shell.prompt;

public final class OrderPromptSupport {
    protected static void exitAction() {
        System.out.printf(OrderPromptStrings.EXIT_MESSAGE);
        System.exit(0);
    }

    protected static String removeDecimalZero(Double value) {
        var formattedValue = String.format("%.1f", value);
        return formattedValue.endsWith(".0") ? formattedValue.replace(".0", "") : formattedValue;
    }
}
