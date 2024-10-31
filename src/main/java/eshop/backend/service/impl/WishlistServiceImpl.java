package eshop.backend.service.impl;

import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.exception.VariantNotFoundException;
import eshop.backend.exception.WishlistNotFoundException;
import eshop.backend.model.Wishlist;
import eshop.backend.repository.ProductRepository;
import eshop.backend.repository.UserRepository;
import eshop.backend.repository.WishlistRepository;
import eshop.backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static eshop.backend.utils.EntityUtils.findByEmailOrElseThrow;
import static eshop.backend.utils.EntityUtils.findByIdOrElseThrow;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public Wishlist createByUserEmail(String email) throws UserNotFoundException {
        var user = findByEmailOrElseThrow(email, userRepository);

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);

        return wishlistRepository.save(wishlist);
    }

    @Override
    public Wishlist readByUserEmail(String email) throws UserNotFoundException {
        var user = findByEmailOrElseThrow(email, userRepository);

        return wishlistRepository.findByUser(user);
    }

    @Override
    public Wishlist addItemByUserEmail(String email, Long productId) throws UserNotFoundException, ProductNotFoundException {
        var wishlist = readByUserEmail(email);
        var product = findByIdOrElseThrow(productId, productRepository, ProductNotFoundException::new);

        wishlist.addItem(product);

        return wishlistRepository.save(wishlist);
    }

    @Override
    public void removeItemByUserEmail(String email, Long productId) throws UserNotFoundException, ProductNotFoundException {
        var wishlist = readByUserEmail(email);
        var product = findByIdOrElseThrow(productId, productRepository, ProductNotFoundException::new);

        wishlist.removeItem(product);

        wishlistRepository.save(wishlist);
    }

    @Override
    public void delete(Long wishlistId) throws WishlistNotFoundException {
        var wishlist = findByIdOrElseThrow(wishlistId, wishlistRepository, WishlistNotFoundException::new);

        wishlistRepository.delete(wishlist);
    } //todo: použít u delete user
}