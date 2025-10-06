package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.ProductRequest;
import likeUniquloWeb.dto.request.ProductUpdateRequest;
import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.ImageResponse;
import likeUniquloWeb.dto.response.ProductResponse;
import likeUniquloWeb.entity.*;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.ImageMapper;
import likeUniquloWeb.mapper.ProductMapper;
import likeUniquloWeb.mapper.VariantMapper;
import likeUniquloWeb.repository.CategoryRepository;
import likeUniquloWeb.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
    VariantMapper variantMapper;

//    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse create(ProductRequest request) throws IOException {
        try{
        validateProductRequest(request);
        Product product = mapper.toEntity(request);
        Long categoryId = request.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        product.setCategory(category);
            if (request.getProductVariants() != null && !request.getProductVariants().isEmpty()) {
                Set<ProductVariant> variants = new HashSet<>();
                for (VariantRequest variantRequest : request.getProductVariants()) {
                    ProductVariant variant = variantMapper.toEntity(variantRequest);
                    variant.setProduct(product);
                    if (variantRequest.getStock() != null) {
                        Stock stock = new Stock();
                        stock.setQuantity(variantRequest.getStock().getQuantity());
                        stock.setWarehouseCode(variantRequest.getStock().getWarehouseCode());
                        stock.setProductVariant(variant);
                        variant.setStock(stock);
                    }
                    variants.add(variant);
                }
                product.setProductVariants(variants);
            }
        repository.save(product);
        if(request.getImages() !=null && !request.getImages().isEmpty()){
            List<ImageResponse> imageResponses =
                    imageService.upLoadProductImages(product.getId(), request.getImages());
            List<Image> images = imageMapper.imgToEntity(imageResponses);
            images.forEach(image -> image.setProduct(product));
            product.setImages(images);
            repository.save(product);
        }
        return mapper.toDto(product);}catch (Exception e){
            log.error("error", e);
            throw  e;
        }
    }

    public ProductResponse createProduct(ProductRequest request, List<MultipartFile> files) throws IOException {
        validateProductRequest(request);
        Product product = mapper.toEntity(request);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        product.setCategory(category);

        if (request.getProductVariants() != null && !request.getProductVariants().isEmpty()) {
            Set<ProductVariant> variants = new HashSet<>();
            for (VariantRequest variantRequest : request.getProductVariants()) {
                ProductVariant variant = variantMapper.toEntity(variantRequest);
                variant.setProduct(product);
                if (variantRequest.getStock() != null) {
                    Stock stock = new Stock();
                    stock.setQuantity(variantRequest.getStock().getQuantity());
                    stock.setWarehouseCode(variantRequest.getStock().getWarehouseCode());
                    stock.setProductVariant(variant);
                    variant.setStock(stock);
                }
                variants.add(variant);
            }
            product.setProductVariants(variants);
        }

        repository.save(product);

        if (files != null && !files.isEmpty()) {
            List<ImageResponse> imageResponses = imageService.upLoadProductImages(product.getId(), files);
            List<Image> images = imageMapper.imgToEntity(imageResponses);
            images.forEach(image -> image.setProduct(product));
            product.setImages(images);
            repository.save(product);
        }

        return mapper.toDto(product);
    }

    public List<ProductResponse> searchProductsByName(String keyword, Pageable pageable) {
        return repository.searchByName(keyword, pageable).stream().map(mapper::toDto).toList();
    }


    //    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponse> getAllProducts(){
        List<Product> products = repository.findAll();
        for (Product p : products) {
            System.out.println("Product: " + p.getName() + " - Variants: "
                    + p.getProductVariants().size());
        }
        return repository.findAll().stream().map(product ->
                mapper.toDto(product)
                ).toList();
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponse> getProductsByCategory(String categoryName){
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_MUST_NOT_BE_NULL);
        }
        Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return category.getProducts().stream()
                .map(mapper::toDto).toList();
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse getById(Long id){
        Product product = repository.findById(id).orElseThrow(()->
                new RuntimeException(ErrorCode.PRODUCT_NOT_FOUND.getMessage())
                );
        ProductResponse response = mapper.toDto(product);

        List<ImageResponse> imageResponses = product.getImages().stream()
                .map(img ->
                        new ImageResponse(img.getId(), img.getUrl(),
                                img.getFileName(), img.getAltText(),
                                img.getProduct().getName(),
                                img.getProduct().getId())).toList()
                        ;

        response.setImages(imageResponses);
        return response;
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id){
        Product product = repository.findById(id)
                        .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        repository.deleteById(id);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(ProductUpdateRequest request, Long id) throws IOException {
        validateProductUpdateRequest(request);
        Product product = repository.findById(id).orElseThrow(()->
                        new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        mapper.updateProduct(request, product);
        if(!product.getCategory().getId().equals(request.getCategoryId())){
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }

        if (request.getProductVariants() != null && !request.getProductVariants().isEmpty()) {
            product.getProductVariants().clear();
            Set<ProductVariant> variants = request.getProductVariants().stream()
                    .map(variantRequest -> {
                        ProductVariant variant = variantMapper.toEntity(variantRequest);
                        variant.setProduct(product);
                        return variant;
                    })
                    .collect(Collectors.toSet());

            product.getProductVariants().addAll(variants);
        }

        Product savedOne = repository.saveAndFlush(product);
        handlingImageUpdate(request.getNewImages(), savedOne, request.getDeleteImageIds());

        return mapper.toDto(repository.save(product));
}

//    private void handlingImageUpdate(List<MultipartFile> newImages, Product product)
//            throws IOException {
//        if(newImages == null || newImages.isEmpty()){
//            return;
//        }
//
//        if(shouldReplaceAllImages(newImages)){
//            product.getImages().clear();
//            List<ImageResponse> imageResponses = imageService.upLoadProductImages(product.getId(), newImages);
//            List<Image> images = imageMapper.imgToEntity(imageResponses);
//            images.forEach(image -> image.setProduct(product));
//            product.getImages().addAll(images);
//
//        }else {
//            List<ImageResponse> imageResponses = imageService.upLoadProductImages(product.getId(), newImages);
//            List<Image> newImageEntities = imageMapper.imgToEntity(imageResponses);
//            newImageEntities.forEach(i->i.setProduct(product));
//            product.getImages().addAll(newImageEntities);
//        }
//    }

    private void handlingImageUpdate(List<MultipartFile> newImages, Product product, List<Long> deleteImageIds)
            throws IOException {

        // ✅ Lọc bỏ null values và validate
        if(deleteImageIds != null && !deleteImageIds.isEmpty()){
            List<Long> validIds = deleteImageIds.stream()
                    .filter(id -> id != null && id > 0)
                    .collect(Collectors.toList());

            if(!validIds.isEmpty()) {
                product.getImages().removeIf(img -> validIds.contains(img.getId()));
                validIds.forEach(id -> {
                    try {
                        imageService.delete(id);
                    } catch (Exception e) {
                        log.warn("Failed to delete image with id {}: {}", id, e.getMessage());
                        // Không throw exception để không làm fail toàn bộ request
                    }
                });
            }
        }

        // Thêm ảnh mới
        if(newImages != null && !newImages.isEmpty()){
            List<ImageResponse> imageResponses = imageService.upLoadProductImages(product.getId(), newImages);
            List<Image> newImageEntities = imageMapper.imgToEntity(imageResponses);
            newImageEntities.forEach(i -> i.setProduct(product));
            product.getImages().addAll(newImageEntities);
        }
    }


    private boolean shouldReplaceAllImages(List<MultipartFile> newImages){
        return true;
    }

    private void validateProductRequest(ProductRequest request){
        if(request == null){
            throw new AppException(ErrorCode.PRODUCT_MUST_NOT_BE_NULL);
        }

        if(request.getName() == null || request.getName().trim().isEmpty()){
            throw  new AppException(ErrorCode.PRODUCT_NAME_MUST_NOT_BE_NULL);
        }

        if(request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw  new AppException(ErrorCode.PRODUCT_PRICE_MUST_NOT_BE_NULL_AND_ABOVE_ZERO);
        }
    }

    private void validateProductUpdateRequest(ProductUpdateRequest request){
        if(request == null){
            throw new AppException(ErrorCode.PRODUCT_MUST_NOT_BE_NULL);
        }

        if(request.getName() == null || request.getName().trim().isEmpty()){
            throw  new AppException(ErrorCode.PRODUCT_NAME_MUST_NOT_BE_NULL);
        }

        if(request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw  new AppException(ErrorCode.PRODUCT_PRICE_MUST_NOT_BE_NULL_AND_ABOVE_ZERO);
        }
    }

    public Page<ProductResponse> searchProducts(String keyword, String category, Pageable pageable) {
        Page<Product> products = repository.searchByNameAndCategory(keyword, category, pageable);
        return products.map(mapper::toDto);
    }
}
