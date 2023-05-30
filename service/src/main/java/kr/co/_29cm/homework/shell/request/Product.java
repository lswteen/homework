package kr.co._29cm.homework.shell.request;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"productId"})
public class Product {
    private Long productId;
    private String name;
    private Double price;
    private Integer stock;
}
