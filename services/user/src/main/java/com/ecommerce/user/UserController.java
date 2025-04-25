package com.ecommerce.user;

import com.ecommerce.authorization.Role;
import com.ecommerce.settings.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = Constants.PAGE_NUMBER + "") int pageNumber,
            @RequestParam(defaultValue = Constants.PAGE_SIZE + "") int pageSize,
            @RequestParam(defaultValue = Constants.DIRECTION) String direction,
            @RequestParam(required = false, defaultValue = Constants.DEFAULT_SORT_ATTRIBUTE) String attribute
    ) {
        log.info("Fetching all users, page number: {}, page size: {}", pageNumber, pageSize);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), attribute);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Page<UserResponse> response = userService.getAllUsers(pageRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long id,
            @RequestBody String role
    ) {
        log.info("Changing role to {} for user with ID: {}", role, id);
        userService.updateUserRole(id, role);
        return ResponseEntity.accepted().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        log.info("Request to delete user with ID: {}", id);
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
