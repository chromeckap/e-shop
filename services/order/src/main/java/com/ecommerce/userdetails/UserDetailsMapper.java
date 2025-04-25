package com.ecommerce.userdetails;

import com.ecommerce.address.AddressMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDetailsMapper {
    private final AddressMapper addressMapper;

    public UserDetails toUserDetails(@NonNull UserDetailsRequest request) {
        log.debug("Mapping UserDetailsRequest to UserDetails: {}", request);
        return UserDetails.builder()
                .userId(request.id())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

    }

    public UserDetailsResponse toResponse(@NonNull UserDetails userDetails) {
        log.debug("Mapping UserDetails to UserDetailsResponse: {}", userDetails);
        return UserDetailsResponse.builder()
                .id(userDetails.getId())
                .userId(userDetails.getUserId())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .email(userDetails.getEmail())
                .address(addressMapper.toResponse(userDetails.getAddress()))
                .build();
    }
}
