package com.ecommerce.deliverymethod;

import com.ecommerce.strategy.CourierType;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeliveryMethodMapper {

    public DeliveryMethod toDeliveryMethod(@NonNull DeliveryMethodRequest request) {
        log.debug("Mapping DeliveryMethodRequest to DeliveryMethod: {}", request);
        return DeliveryMethod.builder()
                .id(request.id())
                .name(request.name())
                .isActive(request.isActive())
                .price(request.price())
                .isFreeForOrderAbove(request.isFreeForOrderAbove())
                .freeForOrderAbove(request.freeForOrderAbove())
                .courierType(request.type())
                .build();
    }

    public DeliveryMethodResponse toResponse(@NonNull DeliveryMethod deliveryMethod) {
        log.debug("Mapping DeliveryMethod to DeliveryMethodResponse: {}", deliveryMethod);
        return DeliveryMethodResponse.builder()
                .id(deliveryMethod.getId())
                .name(deliveryMethod.getName())
                .courierType(CourierType.getType(deliveryMethod))
                .isActive(deliveryMethod.isActive())
                .price(deliveryMethod.getPrice())
                .isFreeForOrderAbove(deliveryMethod.isFreeForOrderAbove())
                .freeForOrderAbove(deliveryMethod.getFreeForOrderAbove())
                .build();
    }
}
