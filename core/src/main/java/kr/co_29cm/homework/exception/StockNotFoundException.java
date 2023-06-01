package kr.co_29cm.homework.exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(Long productId){
        super("StockNotFoundException 발생. 요청한 상품 재고가 없습니다. :" + productId);
    }
}
