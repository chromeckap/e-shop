package com.ecommerce.delivery;

import com.ecommerce.deliverymethod.DeliveryMethod;
import com.ecommerce.deliverymethod.DeliveryMethodService;
import com.ecommerce.strategy.CourierType;
import com.ecommerce.strategy.DeliveryStrategy;
import com.ecommerce.strategy.DeliveryStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final DeliveryMethodService deliveryMethodService;
    private final DeliveryStrategyFactory deliveryStrategyFactory;

    /**
     * Creates a new delivery using the provided delivery request.
     * The method uses a delivery strategy to process the delivery and then saves the delivery.
     *
     * @param request the delivery request containing necessary details
     * @return the ID of the saved delivery
     */
    @Transactional
    public Long createDelivery(DeliveryRequest request) {
        Objects.requireNonNull(request, "Požadavek na doručení nesmí být prázdný");
        log.debug("Creating delivery for order ID: {}", request.orderId());

        DeliveryMethod deliveryMethod = deliveryMethodService.findDeliveryMethodById(request.deliveryMethodId());
        CourierType courierType = deliveryMethod.getCourierType();
        Delivery delivery = deliveryMapper.toDelivery(request);
        delivery.setMethod(deliveryMethod);

        DeliveryStrategy strategy = deliveryStrategyFactory.getStrategy(courierType);
        strategy.processDelivery(delivery);

        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.debug("Delivery saved with ID: {}", savedDelivery.getId());

        return savedDelivery.getId();
    }
}
