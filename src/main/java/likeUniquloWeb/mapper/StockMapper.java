package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.StockRequest;
import likeUniquloWeb.dto.response.StockResponse;
import likeUniquloWeb.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface StockMapper {

    @Mapping(target = "lastUpdated", ignore = true)
    Stock toEntity(StockRequest request);

    @Mapping(target = "variant", source = "productVariant")
    StockResponse toDto(Stock stock);
    void update(StockRequest request, @MappingTarget Stock stock);

}
