package kr.co._29cm.homework.shell.prompt;

public final class OrderPromptSupport {
    private static final double DELIVERY_FEE = 2500; // 배송비 설정

    protected static void exitAction() {
        System.out.printf(OrderPromptStrings.EXIT_MESSAGE);
        System.exit(0);
    }

    protected static void errorAction(){
        System.out.printf(OrderPromptStrings.INPUT_ERROR_MESSAGE);
    }

    protected static String removeDecimalZero(Double value) {
        var formattedValue = String.format("%.1f", value);
        return formattedValue.endsWith(".0") ? formattedValue.replace(".0", "") : formattedValue;
    }

    protected static double deliveryCharge(double totalOrderPrice) {
        if (totalOrderPrice < 50000 && totalOrderPrice > 0) {
            totalOrderPrice += DELIVERY_FEE;
        }
        return totalOrderPrice;
    }
}
