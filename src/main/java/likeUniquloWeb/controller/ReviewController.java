package likeUniquloWeb.controller;

import likeUniquloWeb.dto.request.ReviewRequest;
import likeUniquloWeb.dto.response.ReviewResponse;
import likeUniquloWeb.service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@CrossOrigin(origins = "*")
public class ReviewController {

    ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token
    ) {
        ReviewResponse response = reviewService.createReview(request, token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewByProduct(
            @PathVariable Long productId
    ) {
        List<ReviewResponse> responses = reviewService.getReviewByProduct(productId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token
    ) {
        ReviewResponse response = reviewService.updateReview(id, request, token);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        reviewService.deleteReview(id, token);
        return ResponseEntity.noContent().build();
    }
}

