package com.ecommerce.user;

import com.ecommerce.authentication.RegisterRequest;
import com.ecommerce.authorization.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    private static final Role DEFAULT_ROLE = Role.CUSTOMER;

    public User toUser(@NonNull RegisterRequest request) {
        log.debug("Mapping RegisterRequest to User: {}", request.email());
        return User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(DEFAULT_ROLE)
                .build();
    }

    public UserResponse toResponse(@NonNull User user) {
        log.debug("Mapping User to UserResponse: {}", user.getEmail());
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createTime(user.getCreateTime())
                .build();
    }

}
