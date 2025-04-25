package com.ecommerce.strategy;

import com.ecommerce.delivery.Delivery;

public interface DeliveryStrategy {
    void processDelivery(Delivery delivery);

    String getWidgetUrl();
}
