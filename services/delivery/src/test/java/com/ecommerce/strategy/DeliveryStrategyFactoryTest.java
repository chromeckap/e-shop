package com.ecommerce.strategy;

import com.ecommerce.balikovna.BalikovnaDeliveryStrategy;
import com.ecommerce.other.OtherDeliveryStrategy;
import com.ecommerce.packeta.PacketaDeliveryStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryStrategyFactoryTest {

    private DeliveryStrategyFactory deliveryStrategyFactory;

    @Mock
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        // Create strategies for testing
        BalikovnaDeliveryStrategy balikovnaStrategy = new BalikovnaDeliveryStrategy();
        PacketaDeliveryStrategy packetaStrategy = new PacketaDeliveryStrategy();
        OtherDeliveryStrategy otherStrategy = new OtherDeliveryStrategy();

        // Prepare mock beans
        Map<String, Object> mockBeans = new HashMap<>();
        mockBeans.put("balikovnaStrategy", balikovnaStrategy);
        mockBeans.put("packetaStrategy", packetaStrategy);
        mockBeans.put("otherStrategy", otherStrategy);

        // Mock application context to return our strategies
        when(applicationContext.getBeansWithAnnotation(CourierHandler.class)).thenReturn(mockBeans);

        // Create factory and initialize
        deliveryStrategyFactory = new DeliveryStrategyFactory(applicationContext);
        deliveryStrategyFactory.init();
    }

    @Test
    void testGetStrategy_Balikovna() {
        DeliveryStrategy strategy = deliveryStrategyFactory.getStrategy(CourierType.BALIKOVNA);
        assertNotNull(strategy);
        assertInstanceOf(BalikovnaDeliveryStrategy.class, strategy);
        assertEquals("https://b2c.cpost.cz/locations/?type=BALIKOVNY", strategy.getWidgetUrl());
    }

    @Test
    void testGetStrategy_Packeta() {
        DeliveryStrategy strategy = deliveryStrategyFactory.getStrategy(CourierType.PACKETA);
        assertNotNull(strategy);
        assertInstanceOf(PacketaDeliveryStrategy.class, strategy);
        assertEquals("https://widget.packeta.com/v6/", strategy.getWidgetUrl());
    }

    @Test
    void testGetStrategy_Other() {
        DeliveryStrategy strategy = deliveryStrategyFactory.getStrategy(CourierType.OTHER);
        assertNotNull(strategy);
        assertInstanceOf(OtherDeliveryStrategy.class, strategy);
        assertNull(strategy.getWidgetUrl());
    }

    @Test
    void testGetStrategy_NonExistentType() {
        assertThrows(IllegalArgumentException.class, () ->
                deliveryStrategyFactory.getStrategy(null)
        );
    }
}