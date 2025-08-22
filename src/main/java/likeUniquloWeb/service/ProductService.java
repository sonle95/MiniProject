package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.ProductRequest;
import likeUniquloWeb.dto.response.ProductResponse;
import likeUniquloWeb.entity.Image;
import likeUniquloWeb.entity.Product;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.FileUploadUtil;
import likeUniquloWeb.mapper.ProductMapper;
import likeUniquloWeb.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository repository;
    ProductMapper mapper;
    public ProductResponse createProduct(ProductRequest request) throws IOException {
        Product product = mapper.toEntity(request);
        return mapper.toDto(repository.save(product));
    }

    public List<ProductResponse> getAllProducts(){
        return repository.findAll().stream().map(product ->
                mapper.toDto(product)
                ).toList();
    }

    public ProductResponse getById(Long id){
        Product product = repository.findById(id).orElseThrow(()->
                new RuntimeException(ErrorCode.PRODUCT_NOT_FOUND.getMessage())
                );
        return mapper.toDto(product);
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }

    public ProductResponse updateProduct(ProductRequest request, Long id) throws IOException {
        Product product = repository.findById(id).orElseThrow(()->
                        new RuntimeException(ErrorCode.PRODUCT_NOT_FOUND.getMessage()));
        mapper.updateProduct(request, product);
        return mapper.toDto(repository.save(product));
}
}
