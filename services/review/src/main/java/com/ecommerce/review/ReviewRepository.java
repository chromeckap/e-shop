package com.ecommerce.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Retrieves all reviews associated with a specific product.
     *
     * @param productId   the ID of the product to find reviews for.
     * @param pageRequest the request of page
     * @return a list of reviews associated with the given product ID.
     */
    Page<Review> findAllByProductId(Long productId, PageRequest pageRequest);

    /**
     * Counts all reviews associated with a specific product.
     *
     * @param productId the ID of the product to count reviews for.
     * @return the total number of reviews for the given product.
     */
    int countAllByProductId(Long productId);

    /**
     * Counts the number of reviews with a specific rating for a given product.
     *
     * @param productId the ID of the product to count reviews for.
     * @param rating    the rating value to filter reviews by.
     * @return the number of reviews with the specified rating for the given product.
     */
    int countByProductIdAndRating(Long productId, int rating);

    /**
     * Checks if a user has already reviewed a specific product.
     *
     * @param userId    the ID of the user to check.
     * @param productId the ID of the product to check.
     * @return true if the user has already reviewed the product, false otherwise.
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
