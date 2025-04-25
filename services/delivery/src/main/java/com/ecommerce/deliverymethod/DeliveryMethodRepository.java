package com.ecommerce.deliverymethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, Long> {

    /**
     * Finds all active delivery methods.
     *
     * @param isActive the status of the delivery methods (active or inactive).
     * @return a list of active delivery methods.
     */
    List<DeliveryMethod> findAllByIsActive(boolean isActive);
}
