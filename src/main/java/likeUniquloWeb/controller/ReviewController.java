package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.ReviewRequest;
import likeUniquloWeb.dto.response.ApiResponse;
import likeUniquloWeb.dto.response.ReviewResponse;
import likeUniquloWeb.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;
    @PostMapping
    public ApiResponse<ReviewResponse> create(@RequestBody ReviewRequest request){
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.createReview(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ReviewResponse>> getAll(){
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.getAllReviews())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ReviewResponse> getById(@PathVariable Long id){
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ReviewResponse> update(@RequestBody ReviewRequest request,
                                                @PathVariable Long id){
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.updateReview(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        reviewService.deleteReview(id);
        return ApiResponse.<Void>builder()
                .message("deleted")
                .build();
    }
}
