package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.ProductRequest;
import likeUniquloWeb.dto.response.ProductResponse;
import likeUniquloWeb.service.ProductService;
import lombok.AccessLevel;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "*")
public class ProductController {
    ProductService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse create(
            @RequestPart("product") ProductRequest request,
            @RequestPart("files") List<MultipartFile> files
    ) throws IOException {
        return service.createProduct(request, files);
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return service.getAllProducts();
    }

    @GetMapping("/search")
    public List<ProductResponse> searchProductByName(@RequestParam String keyword
    ,  @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return service.searchProductsByName(keyword, pageable);
    }
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id){
        return service.getById(id);
    }
//
//    @PutMapping("/{id}")
//    public ProductResponse update(@PathVariable Long id, @RequestBody ProductRequest request) throws IOException {
//        return service.updateProduct(request,id);
//    }

    @GetMapping("/category/{name}")
    public List<ProductResponse> getProductsByCategory(@PathVariable String name) {
        return service.getProductsByCategory(name);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse update(
            @PathVariable Long id,
            @RequestPart("product") ProductRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        request.setImages(images);
        return service.updateProduct(request, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteById(id);
    }

    @GetMapping("/page")
    public Page<ProductResponse> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ProductResponse> result = service.searchProducts(keyword, category, pageable);
        return result;
    }

}
