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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

//    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryMapper.categoryToEntity(request);
        return categoryMapper.categoryToDto(categoryRepository.save(category));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryResponse> getAllCategories(){
        return categoryRepository.findAll().stream().map(categoryMapper::categoryToDto).toList();
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse getById(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new AppException(ErrorCode.CATEGORY_NOT_FOUND)
                );

        return categoryMapper.categoryToDto(category);
    }


//    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(CategoryRequest request, Long id){
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new AppException(ErrorCode.CATEGORY_NOT_FOUND)
        );
        categoryMapper.updateCategory(request,category);
        categoryRepository.save(category);
        return categoryMapper.categoryToDto(category);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id){
        categoryRepository.deleteById(id);
    }


    public Page<CategoryResponse> getCategoriesByPage(int page, int size, String sortDir, String keySearch){
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by("id").ascending()
                : Sort.by("id").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categories;
        if(keySearch == null || keySearch.trim().isEmpty()){
            categories = categoryRepository.findAll(pageable);
        }else {
            categories = categoryRepository.searchByName(keySearch.trim(), pageable);
        }
        return categories.map(categoryMapper::categoryToDto);
    }
}
