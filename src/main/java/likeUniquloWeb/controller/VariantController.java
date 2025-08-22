package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.VariantRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.VariantResponse;
import likeUniquloWeb.service.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/variants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VariantController {
    ProductVariantService variantService;
    @PostMapping
    public ApiResponse<VariantResponse> create(@RequestBody VariantRequest request){
        return ApiResponse.<VariantResponse>builder()
                .result(variantService.createVariant(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<VariantResponse>> getAll(){
        return ApiResponse.<List<VariantResponse>>builder()
                .result(variantService.getAllVariants())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<VariantResponse> getById(@PathVariable Long id){
        return ApiResponse.<VariantResponse>builder()
                .result(variantService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<VariantResponse> update(@RequestBody VariantRequest request,
                                              @PathVariable Long id){
        return ApiResponse.<VariantResponse>builder()
                .result(variantService.updateVariant(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        variantService.deleteById(id);
        return ApiResponse.<Void>builder()
                .message("deleted")
                .build();
    }
}
