package kr.co_29cm.homework.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("ProductNotFoundException 발생. 요청하신 상품이 없습니다. : " + productId);
    }
}
