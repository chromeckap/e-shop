package eshop.backend.service;

import eshop.backend.exception.CartIsEmptyException;
import eshop.backend.exception.OrderNotFoundException;
import eshop.backend.exception.UserNotFoundException;
import eshop.backend.exception.VariantNotFoundException;
import eshop.backend.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface OrderService {
    Order create(String email) throws UserNotFoundException, CartIsEmptyException, VariantNotFoundException;
    Order read(Long orderId) throws OrderNotFoundException;
    Order update();
    void delete(Long orderId);
    Page<Order> list(Pageable pageable);
    void cancel(Long orderId) throws OrderNotFoundException;
}
