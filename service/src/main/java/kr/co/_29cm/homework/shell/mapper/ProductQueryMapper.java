package kr.co._29cm.homework.shell.mapper;

import kr.co._29cm.homework.domain.entity.ProductEntity;
import kr.co._29cm.homework.shell.request.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProductQueryMapper {
    ProductQueryMapper INSTANCE = Mappers.getMapper(ProductQueryMapper.class);

    List<Product> toProducts(List<ProductEntity> productEntities);

    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "stock", target = "stock")
    Product toProduct(ProductEntity productEntity);

}
