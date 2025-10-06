package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.StockResponse;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.service.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/variants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "*")
public class VariantController {
    ProductVariantService variantService;
    @PostMapping
    public VariantResponse create(@RequestBody VariantRequest request){
        return variantService.createVariant(request);
    }

    @GetMapping
    public List<VariantResponse> getAll(){
        return variantService.getAllVariants();
    }

    @GetMapping("/{id}")
    public VariantResponse getById(@PathVariable Long id){
        return variantService.getById(id);
    }

    @PutMapping("/{id}")
    public VariantResponse update(@RequestBody VariantRequest request,
                                              @PathVariable Long id){
        return variantService.updateVariant(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        variantService.deleteById(id);
    }

    @GetMapping("/page")
    public Page<VariantResponse> getVariantsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return variantService.getVariantsByPage(page, size, sortDir);
    }
}
