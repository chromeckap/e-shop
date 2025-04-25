package com.ecommerce.balikovna;

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
@CourierHandler(CourierType.BALIKOVNA)
public class BalikovnaDeliveryStrategy implements DeliveryStrategy {

    @Override
    public void processDelivery(Delivery delivery) {
        Objects.requireNonNull(delivery, "Doručení nesmí být prázdné.");

        delivery.setStatus(DeliveryStatus.CREATED);
        log.info("Balikovna delivery successfully created. ID: {}", delivery.getId());
    }

    @Override
    public String getWidgetUrl() {
        return "https://b2c.cpost.cz/locations/?type=BALIKOVNY";
    }
}
