package com.ecommerce.delivery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryControllerTest {

    @Mock
    private DeliveryService deliveryService;

    @InjectMocks
    private DeliveryController deliveryController;

    private DeliveryRequest validRequest;

    @BeforeEach
    void setUp() {
        // Prepare a valid request for testing
        validRequest = new DeliveryRequest(1L, 1L); // Adjust based on actual DeliveryRequest structure
    }

    @Test
    void testCreateDelivery_Success() {
        // Arrange
        Long expectedDeliveryId = 1L;
        when(deliveryService.createDelivery(validRequest)).thenReturn(expectedDeliveryId);

        // Act
        ResponseEntity<Long> response = deliveryController.createDelivery(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDeliveryId, response.getBody());

        // Verify service method was called
        verify(deliveryService).createDelivery(validRequest);
    }

    @Test
    void testControllerAnnotations() {
        // Verify class-level annotations
        assertNotNull(DeliveryController.class.getAnnotation(RestController.class));
        assertNotNull(DeliveryController.class.getAnnotation(RequestMapping.class));
        assertNotNull(DeliveryController.class.getAnnotation(Validated.class));

        // Check RequestMapping value
        RequestMapping requestMapping = DeliveryController.class.getAnnotation(RequestMapping.class);
        assertArrayEquals(new String[]{"/api/v1/deliveries"}, requestMapping.value());
    }

    @Test
    void testCreateDeliveryMethodAnnotations() throws NoSuchMethodException {
        // Get the createDelivery method
        Method createDeliveryMethod = DeliveryController.class.getMethod("createDelivery", DeliveryRequest.class);

        // Verify method-level annotations
        assertNotNull(createDeliveryMethod.getAnnotation(PostMapping.class));

        // Check method parameters
        var parameters = createDeliveryMethod.getParameters();
        assertEquals(1, parameters.length);

        // Verify parameter annotations
        assertNotNull(parameters[0].getAnnotation(RequestBody.class));
        assertNotNull(parameters[0].getAnnotation(jakarta.validation.Valid.class));
    }

    @Test
    void testLogging() {
        // This test ensures that logging doesn't throw any exceptions
        Long expectedDeliveryId = 1L;
        when(deliveryService.createDelivery(validRequest)).thenReturn(expectedDeliveryId);

        // Act
        ResponseEntity<Long> response = deliveryController.createDelivery(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testConstructorInjection() {
        // Verify that the controller has a constructor with DeliveryService
        assertDoesNotThrow(() -> {
            DeliveryController.class.getConstructor(DeliveryService.class);
        });
    }

    @Test
    void testResponseHandling() {
        // Simulate different service responses
        Long expectedDeliveryId = 1L;
        when(deliveryService.createDelivery(validRequest)).thenReturn(expectedDeliveryId);

        // Act
        ResponseEntity<Long> response = deliveryController.createDelivery(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedDeliveryId, response.getBody());
    }
}