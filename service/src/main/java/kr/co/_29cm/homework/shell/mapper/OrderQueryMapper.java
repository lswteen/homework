package kr.co._29cm.homework.shell.mapper;

import kr.co._29cm.homework.domain.entity.OrderEntity;
import kr.co._29cm.homework.shell.request.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OrderQueryMapper {
    OrderQueryMapper INSTANCE = Mappers.getMapper(OrderQueryMapper.class);

    List<Order> toOrders(List<OrderEntity> orderEntityList);

    @Mapping(source = "product", target = "product")
    Order toOrder(OrderEntity orderEntity);

}
