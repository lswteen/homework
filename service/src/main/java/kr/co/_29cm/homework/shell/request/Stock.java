package kr.co._29cm.homework.shell.request;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"productId"})
public class Stock {
    private Long productId;
    private Integer quantity;
}
