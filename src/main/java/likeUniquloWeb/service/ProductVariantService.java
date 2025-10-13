package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.entity.Order;
import likeUniquloWeb.entity.Product;
import likeUniquloWeb.entity.ProductVariant;
import likeUniquloWeb.entity.Stock;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.VariantMapper;
import likeUniquloWeb.repository.ProductRepository;
import likeUniquloWeb.repository.ProductVariantRepository;
import likeUniquloWeb.repository.StockRepository;
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
public class ProductVariantService {

    ProductVariantRepository variantRepository;
    VariantMapper variantMapper;
    ProductRepository productRepository;
    StockRepository stockRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public VariantResponse createVariant(VariantRequest request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        boolean isDuplicate = variantRepository.existsByProductIdAndColorAndSize(
                request.getProductId(),
                request.getColor(),
                request.getSize()
        );
        if (isDuplicate) {
            throw new AppException(ErrorCode.VARIANT_EXISTED);
        }

        ProductVariant variant = variantMapper.toEntity(request);
        variant.setProduct(product);
        ProductVariant savedVariant = variantRepository.save(variant);


        return variantMapper.toDto(savedVariant);
    }


    public List<VariantResponse> getAllVariants(){
        return variantRepository.findAll().stream().map(variantMapper::toDto).toList();
    }


    public VariantResponse getById(Long id){
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.VARIANT_NOT_FOUND));
        return variantMapper.toDto(variant);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id){
        if (!variantRepository.existsById(id)) {
            throw new AppException(ErrorCode.VARIANT_NOT_FOUND);
        }
        variantRepository.deleteById(id);
    }

//
    @PreAuthorize("hasRole('ADMIN')")
    public VariantResponse updateVariant(Long id, VariantRequest request){
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.VARIANT_NOT_FOUND));

        boolean isDuplicate = variantRepository
                .existsByProductIdAndColorAndSizeAndIdNot(
                        variant.getProduct().getId(),
                        request.getColor(),
                        request.getSize(),
                        id
                );
        if (isDuplicate) {
            throw new AppException(ErrorCode.VARIANT_EXISTED);
        }
        variantMapper.updateVariant(request, variant);

        if (!variant.getProduct().getId().equals(request.getProductId())) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            variant.setProduct(product);
        }

        ProductVariant savedVariant = variantRepository.save(variant);

        return variantMapper.toDto(savedVariant);
    }

    public Page<VariantResponse> getVariantsByPage(int page, int size, String sortDir){
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductVariant> productVariants = sortDir.equalsIgnoreCase("asc")
                ? variantRepository.findAllOrderByStockQuantityAsc(pageable)
                : variantRepository.findAllOrderByStockQuantityDesc(pageable);
        return productVariants.map(variantMapper::toDto);
    }



}
