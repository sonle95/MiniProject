package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.StockRequest;
import likeUniquloWeb.dto.response.StockResponse;
import likeUniquloWeb.entity.Stock;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.StockMapper;
import likeUniquloWeb.repository.StockRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockService {
    StockRepository stockRepository;
    StockMapper stockMapper;

    public StockResponse createStock(StockRequest request){
        return  stockMapper.toDto(stockRepository.save(stockMapper.toEntity(request)));
    }

    public List<StockResponse> getAll(){
        return stockRepository.findAll().stream()
                .map(stockMapper::toDto).toList();
    }

    public StockResponse getById(Long id){
        Stock stock = stockRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        return stockMapper.toDto(stock);
    }

    public StockResponse update(Long id, StockRequest request){
        Stock stock = stockRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        stockMapper.update(request,stock);
        return stockMapper.toDto(stockRepository.save(stock));
    }

    public void delete(Long id){
        stockRepository.deleteById(id);
    }
}
