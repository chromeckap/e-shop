package com.ecommerce.review;

import com.ecommerce.exception.ReviewNotFoundException;
import com.ecommerce.feignclient.product.ProductClient;
import com.ecommerce.feignclient.product.ProductResponse;
import com.ecommerce.security.SecurityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewValidator reviewValidator;
    private final SecurityValidator securityValidator;
    private final ProductClient productClient;

    private static final int MIN_REVIEW_STARS = 1;
    private static final int MAX_REVIEW_STARS = 5;

    /**
     * Finds a review entity by its ID.
     *
     * @param id the review id (must not be null)
     * @return the found Review entity
     * @throws ReviewNotFoundException if no review exists with the given id
     */
    @Transactional(readOnly = true)
    public Review findReviewById(Long id) {
        Objects.requireNonNull(id, "ID recenze nesmí být prázdné.");
        log.debug("Finding review with ID: {}", id);

        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(
                        String.format("Recenze s ID %s nebyla nalezena.", id)
                ));
    }

    /**
     * Retrieves a review response DTO by review ID.
     *
     * @param id the review id (must not be null)
     * @return the ReviewResponse DTO
     */
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {
        Objects.requireNonNull(id, "ID recenze nesmí být prázdné.");
        log.debug("Getting review response for ID: {}", id);

        Review review = this.findReviewById(id);
        return reviewMapper.toResponse(review);
    }

    /**
     * Retrieves all reviews for a given product.
     *
     * @param productId the product id (must not be null)
     * @return a list of ReviewResponse DTOs
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByProductId(Long productId, PageRequest pageRequest) {
        Objects.requireNonNull(productId, "ID produktu nesmí být prázdné.");
        log.debug("Fetching reviews for product ID: {}", productId);

        return reviewRepository.findAllByProductId(productId, pageRequest)
                .map(reviewMapper::toResponse);
    }

    /**
     * Builds a summary of ratings for a product.
     *
     * @param productId the product id (must not be null)
     * @return a ProductRatingSummary containing the total reviews, average rating, and rating counts
     */
    @Transactional(readOnly = true)
    public ProductRatingSummary getSummaryByProductId(Long productId) {
        Objects.requireNonNull(productId, "ID produktu nesmí být prázdné.");
        log.debug("Building rating summary for product ID: {}", productId);

        int totalReviews = reviewRepository.countAllByProductId(productId);

        Map<Integer, Integer> ratingCounts = new HashMap<>();
        int totalRatingSum = 0;

        for (int i = MIN_REVIEW_STARS; i <= MAX_REVIEW_STARS; i++) {
            int count = reviewRepository.countByProductIdAndRating(productId, i);
            ratingCounts.put(i, count);
            totalRatingSum += count * i;
        }

        double averageRating = (double) totalRatingSum / totalReviews;
        return reviewMapper.toSummary(totalReviews, averageRating, ratingCounts);
    }

    /**
     * Creates a new review.
     *
     * @param request the ReviewRequest DTO (must not be null)
     * @return the id of the newly created review
     */
    @Transactional
    public Long createReview(ReviewRequest request) {
        Objects.requireNonNull(request, "Požadavek na recenzi nesmí být prázdný.");
        log.debug("Updating review with request: {}", request);

        securityValidator.validateUserAccess(request.userId());
        reviewValidator.validateReviewIsUnique(request);

        Review review = reviewMapper.toReview(request);
        this.assignReviewDetails(review, request);

        Review savedReview = reviewRepository.save(review);
        log.info("Review successfully saved with ID: {}", savedReview.getId());
        return savedReview.getId();
    }

    /**
     * Updates an existing review.
     *
     * @param id      the id of the review to update (must not be null)
     * @param request the ReviewRequest DTO (must not be null)
     * @return the id of the updated review
     */
    @Transactional
    public Long updateReview(Long id, ReviewRequest request) {
        Objects.requireNonNull(id, "ID recenze nesmí být prázdné.");
        Objects.requireNonNull(request, "Požadavek na recenzi nesmí být prázdný.");

        log.debug("Updating review with ID: {} using request: {}", id, request);

        Review existingReview = this.findReviewById(id);
        securityValidator.validateUserAccess(existingReview.getUserId());

        Review updatedReview = reviewMapper.toReview(request);
        this.assignReviewDetails(updatedReview, request);

        updatedReview.setId(existingReview.getId());

        Review savedReview = reviewRepository.save(updatedReview);
        log.info("Review updated successfully with ID: {}", savedReview.getId());
        return savedReview.getId();
    }

    /**
     * Deletes a review by its id.
     *
     * @param id the review id (must not be null)
     */
    @Transactional
    public void deleteReviewById(Long id) {
        Objects.requireNonNull(id, "ID recenze nesmí být prázdné.");
        log.debug("Deleting review with ID: {}", id);

        boolean isAdmin = securityValidator.userHasRoleAdmin();
        Review review = this.findReviewById(id);

        if (!isAdmin) {
            securityValidator.validateUserAccess(review.getUserId());
        }

        reviewRepository.delete(review);
        log.info("Review with ID {} successfully deleted.", id);
    }

    /**
     * Assigns additional review details (user and product IDs) by calling remote services.
     *
     * @param review  the review entity to update (must not be null)
     * @param request the review request (must not be null)
     */
    private void assignReviewDetails(Review review, ReviewRequest request) {
        Objects.requireNonNull(review, "Recenze nesmí být prázdná.");
        Objects.requireNonNull(request, "Požadavek na recenzi nesmí být prázdný.");

        log.debug("Assigning review details for request: {}", request);

        //var user = userClient.getUserById(request.userId());
        ProductResponse product = productClient.getProductById(request.productId());
        review.setUserId(request.userId());
        review.setProductId(product.id());
    }
}
