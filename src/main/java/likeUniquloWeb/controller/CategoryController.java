package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.CategoryRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.CategoryResponse;
import likeUniquloWeb.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "*")
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    public CategoryResponse create(@RequestBody CategoryRequest request){
        return categoryService.createCategory(request);
    }

    @GetMapping
    public List<CategoryResponse> getAll(){
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable Long id){
        return categoryService.getById(id);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@RequestBody CategoryRequest request,
                                                @PathVariable Long id){
        return categoryService.updateCategory(request, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        categoryService.deleteById(id);
    }

}
