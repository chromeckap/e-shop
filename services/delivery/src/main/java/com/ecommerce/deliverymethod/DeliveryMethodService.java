package com.ecommerce.deliverymethod;

import com.ecommerce.exception.DeliveryMethodNotFoundException;
import com.ecommerce.strategy.CourierType;
import com.ecommerce.strategy.DeliveryStrategy;
import com.ecommerce.strategy.DeliveryStrategyFactory;
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
public class DeliveryMethodService {
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final DeliveryMethodMapper deliveryMethodMapper;
    private final DeliveryMethodValidator deliveryMethodValidator;
    private final DeliveryStrategyFactory deliveryStrategyFactory;

    /**
     * Retrieves a delivery method entity by its ID.
     *
     * @param id the ID of the delivery method
     * @return the delivery method entity
     * @throws DeliveryMethodNotFoundException if no delivery method is found
     */
    @Transactional(readOnly = true)
    public DeliveryMethod findDeliveryMethodById(Long id) {
        Objects.requireNonNull(id, "ID metody pro doručení nesmí být prázdné.");
        log.debug("Fetching delivery method by ID: {}", id);

        DeliveryMethod deliveryMethod = deliveryMethodRepository.findById(id)
                .orElseThrow(() -> new DeliveryMethodNotFoundException(
                        String.format("Metoda doručení s ID %s nebyla nalezena.", id)
                ));
        deliveryMethodValidator.validatedDeliveryMethodAccessible(deliveryMethod);

        return deliveryMethod;
    }

    /**
     * Retrieves a delivery method by its ID.
     *
     * @param id the ID of the delivery method
     * @return the delivery method response
     * @throws DeliveryMethodNotFoundException if no delivery method is found
     */
    @Transactional(readOnly = true)
    public DeliveryMethodResponse getDeliveryMethodById(Long id) {
        Objects.requireNonNull(id, "ID metody pro doručení nesmí být prázdné.");
        log.debug("Fetching delivery method response for ID: {}", id);

        DeliveryMethod deliveryMethod = this.findDeliveryMethodById(id);
        return deliveryMethodMapper.toResponse(deliveryMethod);
    }

    /**
     * Retrieves all delivery methods.
     *
     * @return a list of delivery method responses
     */
    @Transactional(readOnly = true)
    public List<DeliveryMethodResponse> getAllDeliveryMethods() {
        log.debug("Fetching all delivery methods");

        return deliveryMethodRepository.findAll().stream()
                .map(deliveryMethodMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves active delivery methods.
     *
     * @return a list of delivery method responses
     */
    @Transactional(readOnly = true)
    public List<DeliveryMethodResponse> getActiveDeliveryMethods() {
        log.debug("Fetching active delivery methods");

        return deliveryMethodRepository.findAllByIsActive(true).stream()
                .map(deliveryMethodMapper::toResponse)
                .collect(Collectors.toList());
    }

    public String getCourierWidgetUrl(String courierType) {
        DeliveryStrategy strategy = deliveryStrategyFactory.getStrategy(CourierType.valueOf(courierType));
        return strategy.getWidgetUrl();
    }

    /**
     * Retrieves all courier types.
     *
     * @return a list of courier types
     */
    @Transactional(readOnly = true)
    public List<Map<String, String>> getCourierTypes() {
        log.debug("Fetching all courier types");

        return CourierType.getAll();
    }

    /**
     * Creates a new delivery method based on the given request.
     *
     * @param request the delivery method request DTO
     * @return the ID of the created delivery method
     */
    @Transactional
    public Long createDeliveryMethod(DeliveryMethodRequest request) {
        Objects.requireNonNull(request, "Požadavek na metodu pro doručení nesmí být prázdný.");
        log.debug("Creating delivery method with request: {}", request);

        DeliveryMethod deliveryMethod = deliveryMethodMapper.toDeliveryMethod(request);

        DeliveryMethod savedDeliveryMethod = deliveryMethodRepository.save(deliveryMethod);
        log.info("Delivery method created successfully: ID {}, Name {}", savedDeliveryMethod.getId(), savedDeliveryMethod.getName());

        return savedDeliveryMethod.getId();
    }

    /**
     * Updates an existing delivery method based on the given ID and request.
     *
     * @param id      the ID of the delivery method to update
     * @param request the updated delivery method request DTO
     * @return the ID of the updated delivery method
     * @throws DeliveryMethodNotFoundException if no delivery method is found with the given ID
     */
    @Transactional
    public Long updateDeliveryMethod(Long id, DeliveryMethodRequest request) {
        Objects.requireNonNull(id, "ID metody doručení nesmí být prázdné.");
        Objects.requireNonNull(request, "Požadavek na metodu pro doručení nesmí být prázdný.");
        log.debug("Updating delivery method with ID: {} using request: {}", id, request);

        DeliveryMethod existingDeliveryMethod = this.findDeliveryMethodById(id);
        DeliveryMethod updatedDeliveryMethod = deliveryMethodMapper.toDeliveryMethod(request);

        updatedDeliveryMethod.setId(existingDeliveryMethod.getId());
        DeliveryMethod savedDeliveryMethod = deliveryMethodRepository.save(updatedDeliveryMethod);
        log.info("Delivery method updated successfully: ID {}, Name {}", savedDeliveryMethod.getId(), savedDeliveryMethod.getName());

        return savedDeliveryMethod.getId();
    }

    /**
     * Deletes a delivery method by its ID.
     *
     * @param id the ID of the delivery method to delete
     * @throws DeliveryMethodNotFoundException if no delivery method is found with the given ID
     */
    @Transactional
    public void deleteDeliveryMethodById(Long id) {
        Objects.requireNonNull(id, "ID metody doručení nesmí být prázdné.");
        log.debug("Deleting delivery method with ID: {}", id);

        DeliveryMethod deliveryMethod = this.findDeliveryMethodById(id);
        deliveryMethodRepository.delete(deliveryMethod);
        log.info("Delivery method deleted successfully: ID {}, Name {}", id, deliveryMethod.getName());
    }
}
