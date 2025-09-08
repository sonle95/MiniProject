package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.CategoryRequest;
import likeUniquloWeb.dto.response.CategoryResponse;
import likeUniquloWeb.entity.Category;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.CategoryMapper;
import likeUniquloWeb.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        Category category = categoryMapper.categoryToEntity(request);
        return categoryMapper.categoryToDto(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAllCategories(){
        return categoryRepository.findAll().stream().map(categoryMapper::categoryToDto).toList();
    }

    public CategoryResponse getById(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new AppException(ErrorCode.NOT_FOUND)
                );

        return categoryMapper.categoryToDto(category);
    }

    public CategoryResponse updateCategory(CategoryRequest request, Long id){
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new AppException(ErrorCode.NOT_FOUND)
        );
        categoryMapper.updateCategory(request,category);
        categoryRepository.save(category);
        return categoryMapper.categoryToDto(category);
    }

    public void deleteById(Long id){
        categoryRepository.deleteById(id);
    }

}
