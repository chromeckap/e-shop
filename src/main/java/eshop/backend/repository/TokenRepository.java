package eshop.backend.repository;

import eshop.backend.model.Token;
import eshop.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    List<Token> findAllByUser(User user);

    Optional<Token> findByToken(String token);
}
