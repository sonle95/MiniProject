package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.StockRequest;
import likeUniquloWeb.dto.response.StockResponse;
import likeUniquloWeb.entity.ProductVariant;
import likeUniquloWeb.entity.Stock;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.StockMapper;
import likeUniquloWeb.repository.ProductVariantRepository;
import likeUniquloWeb.repository.StockRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockService {
    StockRepository stockRepository;
    StockMapper stockMapper;
    ProductVariantRepository variantRepository;

//    @PreAuthorize("hasRole('ADMIN')")
    public StockResponse createStock(StockRequest request){
        Stock stock = stockMapper.toEntity(request);

        Long variantId = request.getProductVariantId();
        ProductVariant productVariant = variantRepository.findById(variantId)
                .orElseThrow(()-> new AppException(ErrorCode.VARIANT_NOT_FOUND));
        stock.setProductVariant(productVariant);
        return  stockMapper.toDto(stockRepository.save(stock));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public List<StockResponse> getAll(){
        return stockRepository.findAll().stream()
                .map(stockMapper::toDto).toList();
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public StockResponse getById(Long id){
        Stock stock = stockRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.STOCK_NOT_FOUND));
        return stockMapper.toDto(stock);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public StockResponse update(Long id, StockRequest request){
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STOCK_NOT_FOUND));

        stockMapper.update(request, stock);

        Long variantId = request.getProductVariantId();
        if (variantId != null) {
            ProductVariant variant = variantRepository.findById(variantId)
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
            stock.setProductVariant(variant);
        }

        return stockMapper.toDto(stockRepository.save(stock));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Long id){
        stockRepository.deleteById(id);
    }
}
