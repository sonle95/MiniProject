package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.ReviewRequest;
import likeUniquloWeb.dto.response.ReviewResponse;
import likeUniquloWeb.entity.ProductVariant;
import likeUniquloWeb.entity.Review;
import likeUniquloWeb.entity.User;
import likeUniquloWeb.enums.OrderStatus;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.ReviewMapper;
import likeUniquloWeb.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;
    UserRepository userRepository;
    OrderRepository orderRepository;
    ProductVariantRepository productVariantRepository;
    AuthenticationService authenticationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ReviewResponse createReview(ReviewRequest request, String token){
        User user = authenticationService.getUserFromToken(token);
        ProductVariant productVariant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(()-> new AppException(ErrorCode.VARIANT_NOT_FOUND));


        if (reviewRepository.existsByUser_IdAndProduct_Id(user.getId(), productVariant.getProduct().getId())) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        if (!orderRepository.existsByUser_IdAndOrderItems_ProductVariant_IdAndStatus(
                user.getId(), request.getProductVariantId(), OrderStatus.DELIVERED)) {
            throw new AppException(ErrorCode.REVIEW_WITHOUT_PURCHASE);
        }

        Review review = reviewMapper.toEntity(request);
        review.setProduct(productVariant.getProduct());
        review.setUser(user);
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    public List<ReviewResponse> getReviewByProduct(Long productId) {
        List<Review> reviews = reviewRepository.findByProduct_Id(productId);
        return reviews.stream()
                .map(reviewMapper::toDto)
                .toList();
    }


    public List<ReviewResponse> getAllReviews(){
        return reviewRepository.findAll().stream().map(reviewMapper::toDto).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ReviewResponse updateReview(Long id, ReviewRequest request, String token) {
        User user = authenticationService.getUserFromToken(token);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        reviewMapper.updateReview(request, review);
        return reviewMapper.toDto(reviewRepository.save(review));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void deleteReview(Long id, String token){
        User user = authenticationService.getUserFromToken(token);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        reviewRepository.delete(review);

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ReviewResponse getById(Long id){
        Review review = reviewRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.REVIEW_NOT_FOUND));
        return reviewMapper.toDto(review);
    }


}
