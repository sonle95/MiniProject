package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.ProductRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.ProductResponse;
import likeUniquloWeb.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService service;

    @PostMapping
    public ApiResponse<ProductResponse> create(@RequestBody ProductRequest request) throws IOException {

      return ApiResponse.<ProductResponse>builder()
              .result(service.createProduct(request))
              .build();
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAll() {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(service.getAllProducts())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getById(@PathVariable Long id){
        return ApiResponse.<ProductResponse>builder()
                .result(service.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> update(@PathVariable Long id, @RequestBody ProductRequest request) throws IOException {
        return ApiResponse.<ProductResponse>builder()
                .result(service.updateProduct(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        service.deleteById(id);
        return ApiResponse.<Void>builder()
                .message("Deleted!")
                .build();
    }



}
