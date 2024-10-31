package eshop.backend.service.impl;

import eshop.backend.enums.InventoryAction;
import eshop.backend.enums.OrderStatus;
import eshop.backend.exception.*;
import eshop.backend.model.*;
import eshop.backend.repository.OrderRepository;
import eshop.backend.repository.ProductVariantSnapshotRepository;
import eshop.backend.repository.UserRepository;
import eshop.backend.service.CartService;
import eshop.backend.service.CouponService;
import eshop.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static eshop.backend.utils.EntityUtils.findByEmailOrElseThrow;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final InventoryServiceImpl inventoryService;
    private final ProductVariantSnapshotRepository snapshotRepository;
    private final CouponService couponService;

    @Override
    public Order create(String email) throws CartIsEmptyException, VariantNotFoundException, UserNotFoundException {
        var user = findByEmailOrElseThrow(email, userRepository);
        var cart = cartService.readByUserEmail(email);

        if (cart.getCartItems().isEmpty()) {
            throw new CartIsEmptyException();
        }

        var orderItems = convertToOrderItems(cart.getCartItems());
        var order = new Order(user, orderItems);

        cartService.deleteAllItemsByUserEmail(email);
        return orderRepository.save(order);
    }

    @Override
    public Order read(Long orderId) throws OrderNotFoundException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    public Order update() {
        return null;
    }

    @Override
    public void delete(Long orderId) {

    }

    @Override
    public Page<Order> list(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public void cancel(Long orderId) throws OrderNotFoundException {
        var order = read(orderId);

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    private Set<OrderItem> convertToOrderItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> {
                    try {
                        ProductVariantSnapshot snapshot = snapshotRepository.findByProductIdAndVariantId(cartItem.getVariant().getProduct().getId(), cartItem.getVariant().getId())
                                .orElseGet(() -> createSnapshot(cartItem));

                        inventoryService.handleInventory(cartItem.getVariant(), cartItem.getQuantity(), InventoryAction.ORDER_PLACED);
                        return new OrderItem(cartItem, snapshot);

                    } catch (NotEnoughVariantQuantityException exception) {
                        throw new RuntimeException(exception);
                    }
                })
                .collect(Collectors.toSet());
    }

    private ProductVariantSnapshot createSnapshot(CartItem cartItem) {
        ProductVariantSnapshot snapshot = new ProductVariantSnapshot(cartItem);
        snapshotRepository.save(snapshot);
        return snapshot;
    }

}
