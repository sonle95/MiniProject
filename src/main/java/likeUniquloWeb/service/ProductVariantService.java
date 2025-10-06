package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.OrderResponse;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.entity.Order;
import likeUniquloWeb.entity.ProductVariant;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.VariantMapper;
import likeUniquloWeb.repository.ProductVariantRepository;
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

//    @PreAuthorize("hasRole('ADMIN')")
    public VariantResponse createVariant(VariantRequest request){

        ProductVariant variant = variantMapper.toEntity(request);

        return variantMapper.toDto(variantRepository.save(variant));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public List<VariantResponse> getAllVariants(){
        return variantRepository.findAll().stream().map(variantMapper::toDto).toList();
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public VariantResponse getById(Long id){
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.VARIANT_NOT_FOUND));
        return variantMapper.toDto(variant);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id){
        if (!variantRepository.existsById(id)) {
            throw new AppException(ErrorCode.VARIANT_NOT_FOUND);
        }
        variantRepository.deleteById(id);
    }

//
//    @PreAuthorize("hasRole('ADMIN')")
    public VariantResponse updateVariant(Long id, VariantRequest request){
        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.VARIANT_NOT_FOUND));
        variantMapper.updateVariant(request,variant);
        ProductVariant saved = variantRepository.save(variant);
        return variantMapper.toDto(saved);
    }

    public Page<VariantResponse> getVariantsByPage(int page, int size, String sortDir){
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductVariant> productVariants = sortDir.equalsIgnoreCase("asc")
                ? variantRepository.findAllOrderByStockQuantityAsc(pageable)
                : variantRepository.findAllOrderByStockQuantityDesc(pageable);
        return productVariants.map(variantMapper::toDto);
    }



}
