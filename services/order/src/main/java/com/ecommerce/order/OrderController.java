package com.ecommerce.order;

import com.ecommerce.settings.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("Fetching order with ID: {}", id);
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<OrderOverviewResponse>> getAllOrders(
            @RequestParam(defaultValue = Constants.PAGE_NUMBER + "") int pageNumber,
            @RequestParam(defaultValue = Constants.PAGE_SIZE + "") int pageSize,
            @RequestParam(defaultValue = Constants.DIRECTION) String direction,
            @RequestParam(required = false, defaultValue = Constants.DEFAULT_SORT_ATTRIBUTE) String attribute
    ) {
        log.info("Fetching all orders with page number {} and page size {}", pageNumber, pageSize);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), attribute);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Page<OrderOverviewResponse> response = orderService.getAllOrders(pageRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderOverviewResponse>> getOrdersByUserId(@PathVariable Long userId) {
        log.info("Fetching orders by user ID: {}", userId);
        List<OrderOverviewResponse> response = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody @Valid OrderRequest request) {
        log.info("Creating new order: {}", request);
        Long response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        log.info("Deleting order with ID: {}", id);
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody String status
    ) {
        log.info("Changing status to {} for order with ID: {}", status, id);
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.accepted().build();
    }
}
