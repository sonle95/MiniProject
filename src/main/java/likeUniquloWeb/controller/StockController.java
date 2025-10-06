package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.StockRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.StockResponse;
import likeUniquloWeb.entity.Stock;
import likeUniquloWeb.service.StockService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "*")
public class StockController {

    StockService stockService;


    @PostMapping
    public StockResponse create(@RequestBody StockRequest request){
        return stockService.createStock(request);
    }

    @GetMapping
    public List<StockResponse> getAll(){
        return stockService.getAll();
    }

    @GetMapping("/{id}")
    public StockResponse getById(@PathVariable Long id){
        return stockService.getById(id);
    }

    @PutMapping("/{id}")
    public StockResponse update(@RequestBody StockRequest request,
                                              @PathVariable Long id){
        return stockService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        stockService.delete(id);
    }

    @GetMapping("/page")
    public Page<StockResponse> getAllStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return stockService.getAllStocks(page, size, sortDir);
    }
}
