package likeUniquloWeb.service;

import likeUniquloWeb.dto.request.ReviewRequest;
import likeUniquloWeb.dto.response.ReviewResponse;
import likeUniquloWeb.entity.Review;
import likeUniquloWeb.exception.AppException;
import likeUniquloWeb.exception.ErrorCode;
import likeUniquloWeb.mapper.ReviewMapper;
import likeUniquloWeb.repository.ReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ReviewRepository reviewRepository;
    ReviewMapper reviewMapper;

    public ReviewResponse createReview(ReviewRequest request){
        Review review = reviewMapper.toEntity(request);
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    public List<ReviewResponse> getAllReviews(){
        return reviewRepository.findAll().stream().map(reviewMapper::toDto).toList();
    }

    public ReviewResponse updateReview(Long id, ReviewRequest request){
        Review review = reviewRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        reviewMapper.updateReview(request, review);
        reviewRepository.save(review);
        return reviewMapper.toDto(review);
    }

    public void deleteReview(Long id){
        Review review = reviewRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        reviewRepository.deleteById(id);

    }
    public ReviewResponse getById(Long id){
        Review review = reviewRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        return reviewMapper.toDto(review);
    }


}
