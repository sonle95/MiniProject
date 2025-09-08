package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.ProductRequest;
import likeUniquloWeb.dto.response.ImageResponse;
import likeUniquloWeb.dto.response.ProductResponse;
import likeUniquloWeb.entity.Category;
import likeUniquloWeb.entity.Image;
import likeUniquloWeb.entity.Product;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.ImageMapper;
import likeUniquloWeb.mapper.ProductMapper;
import likeUniquloWeb.repository.CategoryRepository;
import likeUniquloWeb.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository repository;
    ProductMapper mapper;
    ImageServiceImpl imageService;
    ImageMapper imageMapper;
    CategoryRepository categoryRepository;
    public ProductResponse createProduct(ProductRequest request) throws IOException {
        log.info("Creating product with name: {}", request.getName());
        validateProductRequest(request);
        Product product = mapper.toEntity(request);
        Long categoryId = request.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        product.setCategory(category);
        repository.save(product);
        if(request.getImages() !=null && !request.getImages().isEmpty()){
            List<ImageResponse> imageResponses =
                    imageService.upLoadProductImages(product.getId(), request.getImages());
            List<Image> images = imageMapper.imgToEntity(imageResponses);
            images.forEach(image -> image.setProduct(product));
            product.setImages(images);
            repository.save(product);
        }
        return mapper.toDto(product);
    }

    public List<ProductResponse> getAllProducts(){
        return repository.findAll().stream().map(product ->
                mapper.toDto(product)
                ).toList();
    }

    public List<ProductResponse> getProductsByCategory(String categoryName){
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        return category.getProducts().stream()
                .map(mapper::toDto).toList();
    }

    public ProductResponse getById(Long id){
        Product product = repository.findById(id).orElseThrow(()->
                new RuntimeException(ErrorCode.PRODUCT_NOT_FOUND.getMessage())
                );
        return mapper.toDto(product);
    }

    public void deleteById(Long id){
        Product product = repository.findById(id)
                        .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        repository.deleteById(id);
    }

    public ProductResponse updateProduct(ProductRequest request, Long id) throws IOException {
        validateProductRequest(request);
        Product product = repository.findById(id).orElseThrow(()->
                        new AppException(ErrorCode.NOT_FOUND));
        mapper.updateProduct(request, product);
        if(!product.getCategory().getId().equals(request.getCategoryId())){
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
            product.setCategory(category);
        }

        handlingImageUpdate(request.getImages(), product);

        return mapper.toDto(repository.save(product));
}
    private void handlingImageUpdate(List<MultipartFile> newImages, Product product) throws IOException {
        if(newImages == null || newImages.isEmpty()){
            return;
        }

        if(shouldReplaceAllImages(newImages)){
            product.getImages().clear();
            List<ImageResponse> imageResponses = imageService.upLoadProductImages(product.getId(), newImages);
            List<Image> images = imageMapper.imgToEntity(imageResponses);
            images.forEach(image -> image.setProduct(product));
            product.getImages().addAll(images);

        }else {
            List<ImageResponse> imageResponses = imageService.upLoadProductImages(product.getId(), newImages);
            List<Image> newImageEntities = imageMapper.imgToEntity(imageResponses);
            newImageEntities.forEach(i->i.setProduct(product));
            product.getImages().addAll(newImageEntities);
        }
    }

    private boolean shouldReplaceAllImages(List<MultipartFile> newImages){
        return true;
    }

    private void validateProductRequest(ProductRequest request){
        if(request == null){
            throw new AppException(ErrorCode.NOT_FOUND);
        }

        if(request.getName() == null || request.getName().trim().isEmpty()){
            throw  new AppException(ErrorCode.NOT_FOUND);
        }

        if(request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw  new AppException(ErrorCode.NOT_FOUND);
        }
    }
}
