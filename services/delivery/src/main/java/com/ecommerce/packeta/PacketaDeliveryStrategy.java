package com.ecommerce.packeta;

import com.ecommerce.delivery.Delivery;
import com.ecommerce.delivery.DeliveryStatus;
import com.ecommerce.strategy.CourierHandler;
import com.ecommerce.strategy.CourierType;
import com.ecommerce.strategy.DeliveryStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@CourierHandler(CourierType.PACKETA)
public class PacketaDeliveryStrategy implements DeliveryStrategy {
    @Override
    public void processDelivery(Delivery delivery) {
        Objects.requireNonNull(delivery, "Doručení nesmí být prázdné.");

        delivery.setStatus(DeliveryStatus.CREATED);
        log.info("Packeta delivery successfully created. ID: {}", delivery.getId());
    }

    @Override
    public String getWidgetUrl() {
        return "https://widget.packeta.com/v6/";
    }
}
