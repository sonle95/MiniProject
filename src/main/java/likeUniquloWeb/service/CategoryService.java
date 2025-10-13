package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.CategoryRequest;
import likeUniquloWeb.dto.response.CategoryResponse;
import likeUniquloWeb.entity.Category;
import likeUniquloWeb.entity.Image;
import likeUniquloWeb.entity.Product;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.CategoryMapper;
import likeUniquloWeb.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse getById(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new AppException(ErrorCode.CATEGORY_NOT_FOUND)
                );

        return categoryMapper.categoryToDto(category);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public CategoryResponse updateCategory(CategoryRequest request, Long id){
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new AppException(ErrorCode.CATEGORY_NOT_FOUND)
        );
        if (categoryRepository.existsByName(request.getName())) {
            Category existingCategory = categoryRepository.findByName(request.getName());
            if (!existingCategory.getId().equals(id)) {
                throw new AppException(ErrorCode.CATEGORY_EXISTED);
            }
        }

        categoryMapper.updateCategory(request,category);
        categoryRepository.save(category);
        return categoryMapper.categoryToDto(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_HAS_PRODUCTS);
        }
        categoryRepository.delete(category);
        log.info("Deleted category: {}", category.getName());
    }


    public Page<CategoryResponse> getCategoriesByPage(int page, int size, String sortDir, String keySearch){
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by("id").ascending()
                : Sort.by("id").descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categories;
        System.out.println("Received keySearch: [" + keySearch + "]");
        System.out.println("Trimmed keySearch is empty: " + (keySearch == null || keySearch.trim().isEmpty()));
        if(keySearch == null || keySearch.trim().isEmpty()){
            categories = categoryRepository.findAll(pageable);
        }else {
            categories = categoryRepository.searchByName(keySearch.trim(), pageable);
        }
        return categories.map(categoryMapper::categoryToDto);
    }
    public List<String> getAvailableImagesForCategory(Long categoryId) {
        Category category = categoryRepository.findByIdWithProducts(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        List<String> imageUrls = new ArrayList<>();

        if (category.getProducts() != null) {
            for (Product product : category.getProducts()) {
                if (product.getImages() != null) {
                    for (Image image : product.getImages()) {
                        if (image.getUrl() != null && !image.getUrl().isEmpty()) {
                            imageUrls.add(image.getUrl());
                        }
                    }
                }
            }
        }

        return imageUrls;
    }
}

