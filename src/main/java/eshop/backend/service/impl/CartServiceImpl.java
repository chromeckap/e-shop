package eshop.backend.service.impl;

import eshop.backend.exception.CartNotFoundException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.exception.VariantNotFoundException;
import eshop.backend.model.Cart;
import eshop.backend.model.CartItem;
import eshop.backend.repository.CartItemRepository;
import eshop.backend.repository.CartRepository;
import eshop.backend.repository.UserRepository;
import eshop.backend.repository.VariantRepository;
import eshop.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static eshop.backend.utils.EntityUtils.findByEmailOrElseThrow;
import static eshop.backend.utils.EntityUtils.findByIdOrElseThrow;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository itemRepository;
    private final VariantRepository variantRepository;
    private final UserRepository userRepository;

    @Override
    public Cart createByUserEmail(String email) throws UserNotFoundException {
        var user = findByEmailOrElseThrow(email, userRepository);

        Cart cart = new Cart();
        cart.setUser(user); //todo request = don't create now, do orchestration by controller

        return cartRepository.save(cart);
    }

    @Override
    public Cart readByUserEmail(String email) throws UserNotFoundException {
        var user = findByEmailOrElseThrow(email, userRepository);

        return cartRepository.findCartByUser(user);
    }

    @Override
    public void addItemByUserEmail(String email, Long variantId) throws UserNotFoundException, VariantNotFoundException {
        var cart = readByUserEmail(email);
        var variant = findByIdOrElseThrow(variantId, variantRepository, VariantNotFoundException::new);
        var cartItem = new CartItem(cart, variant);

        cart.addItem(cartItem);

        itemRepository.save(cartItem);
    }

    @Override
    public void updateItemQuantityByUserEmail(String email, Long variantId, Integer quantity) throws UserNotFoundException, VariantNotFoundException {
        var cart = readByUserEmail(email);
        var variant = findByIdOrElseThrow(variantId, variantRepository, VariantNotFoundException::new);
        var cartItem = itemRepository.findByCartAndVariant(cart, variant);

        cartItem.setQuantity(quantity);

        itemRepository.save(cartItem);
    }

    @Override
    public void removeItemByUserEmail(String email, Long variantId) throws UserNotFoundException, VariantNotFoundException {
        var cart = readByUserEmail(email);
        var variant = findByIdOrElseThrow(variantId, variantRepository, VariantNotFoundException::new);
        var cartItem = itemRepository.findByCartAndVariant(cart, variant);

        cart.removeItem(cartItem);

        itemRepository.delete(cartItem);
    }

    @Override
    public void delete(Long cartId) throws CartNotFoundException {
        var cart = findByIdOrElseThrow(cartId, cartRepository, CartNotFoundException::new);

        cartRepository.delete(cart);
    } //todo: použít u delete user

    @Override
    public void deleteAllItemsByUserEmail(String email) throws UserNotFoundException {
        var user = findByEmailOrElseThrow(email, userRepository);

        itemRepository.deleteAllByCartUser(user);
    }

    private BigDecimal calculateTotalPrice(Cart cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {
            BigDecimal itemPrice = item.getVariant().getBasePrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);
        }

        return totalPrice;
    }
}
