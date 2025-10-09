package likeUniquloWeb.mapper;

import likeUniquloWeb.dto.request.CategoryRequest;
import likeUniquloWeb.dto.response.CategoryResponse;
import likeUniquloWeb.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface CategoryMapper {

     @Mapping(target = "products", ignore = true)
     Category categoryToEntity(CategoryRequest request);

     @Mapping(target = "productCount", expression = "java(getProductCount(category))")
     CategoryResponse categoryToDto(Category category);

     @Mapping(target = "products", ignore = true)
     void updateCategory(CategoryRequest request,@MappingTarget Category category);

     default Integer getProductCount(Category category) {
          return category.getProducts() != null ? category.getProducts().size() : 0;
     }
}
