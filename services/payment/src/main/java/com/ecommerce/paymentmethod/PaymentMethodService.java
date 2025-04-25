package com.ecommerce.paymentmethod;

import com.ecommerce.exception.PaymentMethodNotFoundException;
import com.ecommerce.strategy.PaymentGatewayType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final PaymentMethodValidator paymentMethodValidator;

    /**
     * Retrieves a payment method entity by its ID.
     *
     * @param id the ID of the payment method
     * @return the payment method entity
     * @throws PaymentMethodNotFoundException if no payment method is found
     */
    @Transactional(readOnly = true)
    public PaymentMethod findPaymentMethodById(Long id) {
        Objects.requireNonNull(id, "ID platební metody nesmí být prázdné.");
        log.debug("Fetching payment method by ID: {}", id);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new PaymentMethodNotFoundException(
                        String.format("Metoda pro platbu s ID %s nebyla nalezena.", id)
                ));
        paymentMethodValidator.validatePaymentMethodAccessible(paymentMethod);

        return paymentMethod;
    }

    /**
     * Retrieves a payment method by its ID.
     *
     * @param id the ID of the payment method
     * @return the payment method response
     * @throws PaymentMethodNotFoundException if no payment method is found
     */
    @Transactional(readOnly = true)
    public PaymentMethodResponse getPaymentMethodById(Long id) {
        Objects.requireNonNull(id, "ID platební metody nesmí být prázdné.");
        log.debug("Fetching payment method response for ID: {}", id);

        PaymentMethod paymentMethod = this.findPaymentMethodById(id);
        return paymentMethodMapper.toResponse(paymentMethod);
    }

    /**
     * Retrieves all payment methods.
     *
     * @return a list of payment method responses
     */
    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getAllPaymentMethods() {
        log.debug("Fetching all payment methods");

        return paymentMethodRepository.findAll().stream()
                .map(paymentMethodMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves active payment methods.
     *
     * @return a list of payment method responses
     */
    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> getActivePaymentMethods() {
        log.debug("Fetching active payment methods");

        return paymentMethodRepository.findAllByIsActive(true).stream()
                .map(paymentMethodMapper::toResponse)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves all available payment method types.
     *
     * @return a list of payment method types
     */
    @Transactional(readOnly = true)
    public List<Map<String, String>> getPaymentGatewayTypes() {
        log.debug("Fetching all payment method types");

        return PaymentGatewayType.getAll();
    }

    /**
     * Creates a new payment method based on the given request.
     *
     * @param request the payment method request DTO
     * @return the ID of the created payment method
     */
    @Transactional
    public Long createPaymentMethod(PaymentMethodRequest request) {
        Objects.requireNonNull(request, "Požadavek na platební metodu nesmí být prázdný.");
        log.debug("Creating payment method with request: {}", request);

        PaymentMethod paymentMethod = paymentMethodMapper.toPaymentMethod(request);

        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        log.info("Payment method created successfully: ID {}, Name {}", savedPaymentMethod.getId(), savedPaymentMethod.getName());

        return savedPaymentMethod.getId();
    }

    /**
     * Updates an existing payment method based on the given ID and request.
     *
     * @param id      the ID of the payment method to update
     * @param request the updated payment method request DTO
     * @return the ID of the updated payment method
     * @throws PaymentMethodNotFoundException if no payment method is found with the given ID
     */
    @Transactional
    public Long updatePaymentMethod(Long id, PaymentMethodRequest request) {
        Objects.requireNonNull(id, "ID platební metody nesmí být prázdné.");
        Objects.requireNonNull(request, "Požadavek na platební metodu nesmí být prázdný.");
        log.debug("Updating payment method with ID: {} using request: {}", id, request);

        PaymentMethod existingPaymentMethod = this.findPaymentMethodById(id);
        PaymentMethod updatedPaymentMethod = paymentMethodMapper.toPaymentMethod(request);

        updatedPaymentMethod.setId(existingPaymentMethod.getId());
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(updatedPaymentMethod);
        log.info("Payment method updated successfully: ID {}, Name {}", savedPaymentMethod.getId(), savedPaymentMethod.getName());

        return savedPaymentMethod.getId();
    }

    /**
     * Deletes a payment method by its ID.
     *
     * @param id the ID of the payment method to delete
     * @throws PaymentMethodNotFoundException if no payment method is found with the given ID
     */
    @Transactional
    public void deletePaymentMethodById(Long id) {
        Objects.requireNonNull(id, "ID platební metody nesmí být prázdné.");
        log.debug("Deleting payment method with ID: {}", id);

        PaymentMethod paymentMethod = this.findPaymentMethodById(id);
        paymentMethodRepository.delete(paymentMethod);
        log.info("Payment method deleted successfully: ID {}, Name {}", id, paymentMethod.getName());
    }
}
