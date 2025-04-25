package com.ecommerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds the cart associated with a given user ID.
     *
     * @param userId the ID of the user whose cart is to be retrieved.
     * @return an Optional containing the Cart if found, or an empty Optional if no cart exists for the user.
     */
    Optional<Cart> findByUserId(Long userId);}
