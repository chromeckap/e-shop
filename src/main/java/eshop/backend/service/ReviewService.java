package eshop.backend.service;

import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.exception.ReviewNotFoundException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.model.Product;
import eshop.backend.model.Review;
import eshop.backend.request.ReviewRequest;
import eshop.backend.response.ReviewResponse;
import eshop.backend.response.RatingSummary;

public interface ReviewService {
    Review create(ReviewRequest request) throws ProductNotFoundException, UserNotFoundException;
    ReviewResponse read(Long reviewId) throws ReviewNotFoundException;
    Review update(ReviewRequest request) throws ReviewNotFoundException;
    void delete(Long reviewId) throws ReviewNotFoundException;
    RatingSummary getRatingSummary(Product product);
}
