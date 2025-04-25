package com.ecommerce.address;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AddressMapper {

    public Address toAddress(@NonNull AddressRequest request) {
        log.debug("Mapping AddressRequest to Order: {}", request);
        return Address.builder()
                .city(request.city())
                .street(request.street())
                .postalCode(request.postalCode())
                .build();
    }

    public AddressResponse toResponse(@NonNull Address address) {
        log.debug("Mapping Address to AddressResponse: {}", address);
        return AddressResponse.builder()
                .id(address.getId())
                .city(address.getCity())
                .street(address.getStreet())
                .postalCode(address.getPostalCode())
                .build();
    }
}
