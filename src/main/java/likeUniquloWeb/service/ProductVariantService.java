package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.entity.ProductVariant;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.VariantMapper;
import likeUniquloWeb.repository.ProductVariantRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantService {

    ProductVariantRepository variantRepository;
    VariantMapper variantMapper;

    public VariantResponse createVariant(VariantRequest request){
        ProductVariant variant = variantMapper.toEntity(request);
        return variantMapper.toDto(variantRepository.save(variant));
    }

    public List<VariantResponse> getAllVariants(){
        return variantRepository.findAll().stream().map(variantMapper::toDto).toList();
    }

    public VariantResponse getById(Long id){
        ProductVariant variant = variantRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        return variantMapper.toDto(variant);
    }

    public void deleteById(Long id){
        if (!variantRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        variantRepository.deleteById(id);
    }

    public VariantResponse updateVariant(Long id, VariantRequest request){
        ProductVariant variant = variantRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        variantMapper.updateVariant(request,variant);
        ProductVariant saved = variantRepository.save(variant);
        return variantMapper.toDto(saved);
    }


}
