package com.ecommerce.review;

import com.ecommerce.exception.ReviewExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewValidator {
    private final ReviewRepository reviewRepository;

    public void validateReviewIsUnique(ReviewRequest request) {
        boolean userAlreadyRatedProduct = reviewRepository.existsByUserIdAndProductId(request.userId(), request.productId());

        log.debug("Validating if user {} already reviewed product {}", request.userId(), request.productId());
        if (userAlreadyRatedProduct)
            throw new ReviewExistsException("Pro tento produkt jste již recenzi napsal.");
    }
}
