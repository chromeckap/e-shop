package com.ecommerce.attribute;

import com.ecommerce.exception.AttributeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeControllerTest {

    @Mock
    private AttributeService attributeService;

    @InjectMocks
    private AttributeController attributeController;

    private AttributeResponse testAttributeResponse;
    private AttributeRequest testAttributeRequest;

    @BeforeEach
    void setUp() {
        testAttributeResponse = AttributeResponse.builder()
                .id(1L)
                .name("Color")
                .values(Collections.emptyList())
                .build();

        testAttributeRequest = new AttributeRequest(
                null, // ID is null for creation
                "Color",
                Collections.emptyList()
        );
    }

    @Test
    void getAttributeById_WithValidId_ReturnsOkWithAttribute() {
        // Arrange
        when(attributeService.getAttributeById(1L)).thenReturn(testAttributeResponse);

        // Act
        ResponseEntity<AttributeResponse> response = attributeController.getAttributeById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAttributeResponse, response.getBody());
        verify(attributeService).getAttributeById(1L);
    }

    @Test
    void getAttributeById_WithInvalidId_ThrowsAttributeNotFoundException() {
        // Arrange
        when(attributeService.getAttributeById(999L)).thenThrow(new AttributeNotFoundException("Attribute not found"));

        // Act & Assert
        assertThrows(AttributeNotFoundException.class, () -> attributeController.getAttributeById(999L));
        verify(attributeService).getAttributeById(999L);
    }

    @Test
    void getAllAttributes_ReturnsOkWithAttributes() {
        // Arrange
        Set<AttributeResponse> attributes = Set.of(testAttributeResponse);
        when(attributeService.getAllAttributes()).thenReturn(attributes);

        // Act
        ResponseEntity<Set<AttributeResponse>> response = attributeController.getAllAttributes();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(attributes, response.getBody());
        verify(attributeService).getAllAttributes();
    }

    @Test
    void createAttribute_WithValidRequest_ReturnsCreatedWithId() {
        // Arrange
        Long createdId = 1L;
        when(attributeService.createAttribute(testAttributeRequest)).thenReturn(createdId);

        // Act
        ResponseEntity<Long> response = attributeController.createAttribute(testAttributeRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdId, response.getBody());
        verify(attributeService).createAttribute(testAttributeRequest);
    }

    @Test
    void updateAttribute_WithValidIdAndRequest_ReturnsOkWithId() {
        // Arrange
        Long updatedId = 1L;
        when(attributeService.updateAttribute(1L, testAttributeRequest)).thenReturn(updatedId);

        // Act
        ResponseEntity<Long> response = attributeController.updateAttribute(1L, testAttributeRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedId, response.getBody());
        verify(attributeService).updateAttribute(1L, testAttributeRequest);
    }

    @Test
    void deleteAttributeById_WithValidId_ReturnsNoContent() {
        // Arrange
        doNothing().when(attributeService).deleteAttributeById(1L);

        // Act
        ResponseEntity<Void> response = attributeController.deleteAttributeById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(attributeService).deleteAttributeById(1L);
    }
}