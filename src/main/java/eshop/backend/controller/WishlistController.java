package eshop.backend.controller;

import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.model.Wishlist;
import eshop.backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<Wishlist> read(@AuthenticationPrincipal UserDetails userDetails) throws UserNotFoundException {
        var email = userDetails.getUsername();
        var wishlist = wishlistService.readByUserEmail(email);

        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<Wishlist> addItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long productId) throws UserNotFoundException, ProductNotFoundException {
        var email = userDetails.getUsername();
        var wishlist = wishlistService.addItemByUserEmail(email, productId);

        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long productId) throws UserNotFoundException, ProductNotFoundException {
        var email = userDetails.getUsername();
        wishlistService.removeItemByUserEmail(email, productId);

        return ResponseEntity.noContent().build();
    }
}

