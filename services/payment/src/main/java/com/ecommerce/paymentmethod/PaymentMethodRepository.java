package com.ecommerce.paymentmethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    /**
     * Finds all active payment methods.
     * This method retrieves a list of payment methods where the 'isActive' field is set to true.
     * This is a common query to get only the available payment methods.
     *
     * @param isActive the active status of the payment methods to retrieve.
     * @return a list of active payment methods.
     */
    List<PaymentMethod> findAllByIsActive(boolean isActive);

}
