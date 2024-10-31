package eshop.backend.repository;

import eshop.backend.model.User;
import eshop.backend.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Wishlist findByUser(User user);
}
