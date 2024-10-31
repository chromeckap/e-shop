package eshop.backend.utils;

import eshop.backend.exception.UserNotFoundException;
import eshop.backend.model.User;
import eshop.backend.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.function.Function;

public class EntityUtils {

    public static <T, E extends Exception> T findByIdOrElseThrow(Long id, JpaRepository<T, Long> repository, Function<Long, E> exceptionSupplier) throws E {
        return repository.findById(id)
                .orElseThrow(() -> exceptionSupplier.apply(id));
    }

    public static User findByEmailOrElseThrow(String email, UserRepository repository) throws UserNotFoundException {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }}
