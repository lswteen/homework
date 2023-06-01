package kr.co_29cm.homework.exception;

public class SoldOutException extends RuntimeException {
    public SoldOutException() {
        super("SoldOutException 발생. 주문한 상품 수량이 제고량보다 큽니다.");
    }
}
