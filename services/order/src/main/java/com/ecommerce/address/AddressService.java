package com.ecommerce.address;

import com.ecommerce.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    /**
     * Manages the address for a given order.
     * New address will be created for the order.
     *
     * @param request The address details provided by the user.
     * @param userDetails   The user to associate the address with.
     */
    @Transactional
    public void manageAddress(AddressRequest request, UserDetails userDetails) {
        Address address = addressMapper.toAddress(request);
        address.setUserDetails(userDetails);

        addressRepository.save(address);
    }
}