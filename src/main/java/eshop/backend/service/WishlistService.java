package eshop.backend.service;

import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.exception.VariantNotFoundException;
import eshop.backend.exception.WishlistNotFoundException;
import eshop.backend.model.Wishlist;

public interface WishlistService {
    Wishlist createByUserEmail(String email) throws UserNotFoundException;
    Wishlist readByUserEmail(String email) throws UserNotFoundException;
    Wishlist addItemByUserEmail(String email, Long productId) throws UserNotFoundException, ProductNotFoundException;
    void removeItemByUserEmail(String email, Long productId) throws UserNotFoundException, ProductNotFoundException;
    void delete(Long wishlistId) throws WishlistNotFoundException;
}

//todo přidat dokumentaci