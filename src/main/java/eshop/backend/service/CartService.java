package eshop.backend.service;

import eshop.backend.exception.CartNotFoundException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.exception.VariantNotFoundException;
import eshop.backend.model.Cart;
import org.springframework.stereotype.Service;

@Service
public interface CartService {
    Cart createByUserEmail(String email) throws UserNotFoundException;
    Cart readByUserEmail(String email) throws UserNotFoundException, VariantNotFoundException;
    void addItemByUserEmail(String email, Long variantId) throws UserNotFoundException, VariantNotFoundException;
    void updateItemQuantityByUserEmail(String email, Long variantId, Integer quantity) throws UserNotFoundException, VariantNotFoundException;
    void removeItemByUserEmail(String email, Long variantId) throws UserNotFoundException, VariantNotFoundException;
    void delete(Long cartId) throws CartNotFoundException;
    void deleteAllItemsByUserEmail(String email) throws UserNotFoundException;
}
