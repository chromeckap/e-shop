package com.ecommerce.userdetails;

import com.ecommerce.address.AddressService;
import com.ecommerce.order.Order;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsService {
    private final UserDetailsRepository userDetailsRepository;
    private final UserDetailsMapper userDetailsMapper;
    private final AddressService addressService;

    /**
     * Manages user details for a given order.
     *
     * @param request The user details request.
     * @param order   The associated order.
     */
    @Transactional
    public void manageUserDetails(UserDetailsRequest request, Order order) {
        Objects.requireNonNull(request, "Požadavek na uživatele nesmí být prázdný.");
        Objects.requireNonNull(order, "Objednávka nesmí být prázdná.");

        var userDetails = userDetailsMapper.toUserDetails(request);
        userDetails.setOrder(order);

        var savedUserDetails = userDetailsRepository.save(userDetails);

        addressService.manageAddress(request.address(), savedUserDetails);
    }
}
