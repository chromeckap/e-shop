package com.ecommerce.deliverymethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryMethodControllerTest {

    @Mock
    private DeliveryMethodService deliveryMethodService;

    @InjectMocks
    private DeliveryMethodController deliveryMethodController;

    private DeliveryMethodResponse mockResponse;
    private DeliveryMethodRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockResponse = mock(DeliveryMethodResponse.class);
        mockRequest = mock(DeliveryMethodRequest.class);
    }

    @Test
    void testGetDeliveryMethodById() {
        // Arrange
        Long id = 1L;
        when(deliveryMethodService.getDeliveryMethodById(id)).thenReturn(mockResponse);

        // Act
        ResponseEntity<DeliveryMethodResponse> response = deliveryMethodController.getDeliveryMethodById(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
        verify(deliveryMethodService).getDeliveryMethodById(id);
    }

    @Test
    void testGetAllDeliveryMethods() {
        // Arrange
        List<DeliveryMethodResponse> mockResponses = new ArrayList<>();
        mockResponses.add(mockResponse);
        when(deliveryMethodService.getAllDeliveryMethods()).thenReturn(mockResponses);

        // Act
        ResponseEntity<List<DeliveryMethodResponse>> response = deliveryMethodController.getAllDeliveryMethods();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponses, response.getBody());
        verify(deliveryMethodService).getAllDeliveryMethods();
    }

    @Test
    void testGetActiveDeliveryMethods() {
        // Arrange
        List<DeliveryMethodResponse> mockResponses = new ArrayList<>();
        mockResponses.add(mockResponse);
        when(deliveryMethodService.getActiveDeliveryMethods()).thenReturn(mockResponses);

        // Act
        ResponseEntity<List<DeliveryMethodResponse>> response = deliveryMethodController.getActiveDeliveryMethods();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponses, response.getBody());
        verify(deliveryMethodService).getActiveDeliveryMethods();
    }

    @Test
    void testGetCourierWidgetUrl() {
        // Arrange
        String courierType = "PACKETA";
        String expectedUrl = "https://example.com/widget";
        when(deliveryMethodService.getCourierWidgetUrl(courierType)).thenReturn(expectedUrl);

        // Act
        ResponseEntity<String> response = deliveryMethodController.getCourierWidgetUrl(courierType);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrl, response.getBody());
        verify(deliveryMethodService).getCourierWidgetUrl(courierType);
    }

    @Test
    void testGetCourierTypes() {
        // Arrange
        List<Map<String, String>> mockTypes = new ArrayList<>();
        Map<String, String> mockType = mock(Map.class);
        mockTypes.add(mockType);
        when(deliveryMethodService.getCourierTypes()).thenReturn(mockTypes);

        // Act
        ResponseEntity<List<Map<String, String>>> response = deliveryMethodController.getCourierTypes();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTypes, response.getBody());
        verify(deliveryMethodService).getCourierTypes();
    }

    @Test
    void testCreateDeliveryMethod() {
        // Arrange
        Long expectedId = 1L;
        when(deliveryMethodService.createDeliveryMethod(mockRequest)).thenReturn(expectedId);

        // Act
        ResponseEntity<Long> response = deliveryMethodController.createDeliveryMethod(mockRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedId, response.getBody());
        verify(deliveryMethodService).createDeliveryMethod(mockRequest);
    }

    @Test
    void testUpdateDeliveryMethod() {
        // Arrange
        Long id = 1L;
        Long expectedId = 1L;
        when(deliveryMethodService.updateDeliveryMethod(id, mockRequest)).thenReturn(expectedId);

        // Act
        ResponseEntity<Long> response = deliveryMethodController.updateDeliveryMethod(id, mockRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedId, response.getBody());
        verify(deliveryMethodService).updateDeliveryMethod(id, mockRequest);
    }

    @Test
    void testDeleteDeliveryMethodById() {
        // Arrange
        Long id = 1L;
        doNothing().when(deliveryMethodService).deleteDeliveryMethodById(id);

        // Act
        ResponseEntity<Void> response = deliveryMethodController.deleteDeliveryMethodById(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(deliveryMethodService).deleteDeliveryMethodById(id);
    }

    @Test
    void testControllerAnnotations() {
        // Verify class-level annotations
        assertNotNull(DeliveryMethodController.class.getAnnotation(RestController.class));
        assertNotNull(DeliveryMethodController.class.getAnnotation(RequestMapping.class));
        assertNotNull(DeliveryMethodController.class.getAnnotation(Validated.class));

        // Check RequestMapping value
        RequestMapping requestMapping = DeliveryMethodController.class.getAnnotation(RequestMapping.class);
        assertArrayEquals(new String[]{"/api/v1/delivery-methods"}, requestMapping.value());
    }

    @Test
    void testMethodAnnotations() throws NoSuchMethodException {
        // Verify method-level annotations
        Method[] methods = DeliveryMethodController.class.getDeclaredMethods();

        // Check methods with @PreAuthorize
        String[] adminProtectedMethods = {
                "getAllDeliveryMethods",
                "getCourierTypes",
                "createDeliveryMethod",
                "updateDeliveryMethod",
                "deleteDeliveryMethodById"
        };

        for (String methodName : adminProtectedMethods) {
            PreAuthorize preAuthorize = getPreAuthorize(methodName);
            assertNotNull(preAuthorize, "Method " + methodName + " should have @PreAuthorize annotation");
            assertEquals("hasRole('ADMIN')", preAuthorize.value());
        }
    }

    private static PreAuthorize getPreAuthorize(String methodName) throws NoSuchMethodException {
        Method method = DeliveryMethodController.class.getDeclaredMethod(
                methodName,
                methodName.equals("updateDeliveryMethod")
                        ? new Class<?>[]{Long.class, DeliveryMethodRequest.class}
                        : methodName.equals("deleteDeliveryMethodById")
                        ? new Class<?>[]{Long.class}
                        : methodName.equals("createDeliveryMethod")
                        ? new Class<?>[]{DeliveryMethodRequest.class}
                        : null
        );

        return method.getAnnotation(PreAuthorize.class);
    }
}