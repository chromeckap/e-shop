package com.ecommerce.attributevalue;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.variant.VariantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeValueServiceTest {

    @Mock
    private AttributeValueRepository attributeValueRepository;

    @Mock
    private AttributeValueValidator attributeValueValidator;

    @InjectMocks
    private AttributeValueService attributeValueService;

    @Captor
    private ArgumentCaptor<List<AttributeValue>> attributeValuesCaptor;

    private Attribute testAttribute;
    private AttributeValue testAttributeValue1;
    private AttributeValue testAttributeValue2;
    private AttributeValueRequest testValueRequest1;

    @BeforeEach
    void setUp() {
        testAttribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        testAttributeValue1 = new AttributeValue();
        testAttributeValue1.setId(1L);
        testAttributeValue1.setValue("Red");
        testAttributeValue1.setAttribute(testAttribute);

        testAttributeValue2 = new AttributeValue();
        testAttributeValue2.setId(2L);
        testAttributeValue2.setValue("Blue");
        testAttributeValue2.setAttribute(testAttribute);

        testValueRequest1 = new AttributeValueRequest(1L, "Red");
    }

    @Test
    void processVariantAttributeValues_WithValidRequest_ReturnsAttributeValues() {
        // Arrange
        Set<Long> attributeValueIds = Set.of(1L, 2L);
        List<AttributeValue> attributeValues = List.of(testAttributeValue1, testAttributeValue2);
        VariantRequest request = mock(VariantRequest.class);
        when(request.attributeValueIds()).thenReturn(attributeValueIds);
        when(attributeValueRepository.findAllById(attributeValueIds)).thenReturn(attributeValues);

        // Act
        List<AttributeValue> result = attributeValueService.processVariantAttributeValues(request);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(attributeValueValidator).validateAllAttributeValuesExist(attributeValueIds);
        verify(attributeValueRepository).findAllById(attributeValueIds);
    }

    @Test
    void processVariantAttributeValues_WithEmptyIds_ReturnsEmptyList() {
        // Arrange
        VariantRequest request = mock(VariantRequest.class);
        when(request.attributeValueIds()).thenReturn(Collections.emptySet());

        // Act
        List<AttributeValue> result = attributeValueService.processVariantAttributeValues(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(attributeValueValidator, never()).validateAllAttributeValuesExist(any());
        verify(attributeValueRepository, never()).findAllById(any());
    }

    @Test
    void processVariantAttributeValues_WithNullIds_ReturnsEmptyList() {
        // Arrange
        VariantRequest request = mock(VariantRequest.class);
        when(request.attributeValueIds()).thenReturn(null);

        // Act
        List<AttributeValue> result = attributeValueService.processVariantAttributeValues(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(attributeValueValidator, never()).validateAllAttributeValuesExist(any());
        verify(attributeValueRepository, never()).findAllById(any());
    }

    @Test
    void processVariantAttributeValues_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeValueService.processVariantAttributeValues(null));
        verify(attributeValueValidator, never()).validateAllAttributeValuesExist(any());
        verify(attributeValueRepository, never()).findAllById(any());
    }

    @Test
    void manageAttributeValues_AddNewValues_SavesNewValues() {
        // Arrange
        AttributeValueRequest newValueRequest = new AttributeValueRequest(null, "Green");
        List<AttributeValueRequest> requests = List.of(newValueRequest);

        when(attributeValueRepository.findByAttribute(testAttribute)).thenReturn(Collections.emptyList());

        // Act
        attributeValueService.manageAttributeValues(testAttribute, requests);

        // Assert
        verify(attributeValueRepository).findByAttribute(testAttribute);
        verify(attributeValueRepository).saveAll(attributeValuesCaptor.capture());

        List<AttributeValue> savedValues = attributeValuesCaptor.getValue();
        assertEquals(1, savedValues.size());
        assertEquals("Green", savedValues.getFirst().getValue());
        assertEquals(testAttribute, savedValues.getFirst().getAttribute());

        verify(attributeValueRepository, never()).deleteAll(any());
    }

    @Test
    void manageAttributeValues_UpdateExistingValues_UpdatesExistingValues() {
        // Arrange
        AttributeValueRequest updatedValueRequest = new AttributeValueRequest(1L, "Dark Red");
        List<AttributeValueRequest> requests = List.of(updatedValueRequest);

        when(attributeValueRepository.findByAttribute(testAttribute)).thenReturn(List.of(testAttributeValue1));
        when(attributeValueRepository.existsAttributeValueByIdAndAttribute(1L, testAttribute)).thenReturn(true);

        // Act
        attributeValueService.manageAttributeValues(testAttribute, requests);

        // Assert
        verify(attributeValueRepository).findByAttribute(testAttribute);
        verify(attributeValueRepository).saveAll(attributeValuesCaptor.capture());

        List<AttributeValue> savedValues = attributeValuesCaptor.getValue();
        assertEquals(1, savedValues.size());
        assertEquals(1L, savedValues.getFirst().getId());
        assertEquals("Dark Red", savedValues.getFirst().getValue());

        verify(attributeValueRepository, never()).deleteAll(any());
    }

    @Test
    void manageAttributeValues_DeleteOldValues_DeletesOldValues() {
        // Arrange
        AttributeValueRequest newValueRequest = new AttributeValueRequest(null, "Green");
        List<AttributeValueRequest> requests = List.of(newValueRequest);

        when(attributeValueRepository.findByAttribute(testAttribute)).thenReturn(List.of(testAttributeValue1));

        // Act
        attributeValueService.manageAttributeValues(testAttribute, requests);

        // Assert
        verify(attributeValueRepository).findByAttribute(testAttribute);
        verify(attributeValueRepository).saveAll(attributeValuesCaptor.capture());

        List<AttributeValue> savedValues = attributeValuesCaptor.getValue();
        assertEquals(1, savedValues.size());
        assertEquals("Green", savedValues.getFirst().getValue());

        verify(attributeValueRepository).deleteAll(List.of(testAttributeValue1));
    }

    @Test
    void manageAttributeValues_ComplexScenario_HandlesCorrectly() {
        // Arrange
        // Keep value 1, update value 2, add new value 3, delete value 4
        AttributeValueRequest keepRequest = new AttributeValueRequest(1L, "Red");
        AttributeValueRequest updateRequest = new AttributeValueRequest(2L, "Navy Blue");
        AttributeValueRequest newRequest = new AttributeValueRequest(null, "Green");
        List<AttributeValueRequest> requests = List.of(keepRequest, updateRequest, newRequest);

        AttributeValue valueToDelete = new AttributeValue();
        valueToDelete.setId(4L);
        valueToDelete.setValue("Yellow");
        valueToDelete.setAttribute(testAttribute);

        when(attributeValueRepository.findByAttribute(testAttribute))
                .thenReturn(List.of(testAttributeValue1, testAttributeValue2, valueToDelete));

        when(attributeValueRepository.existsAttributeValueByIdAndAttribute(1L, testAttribute)).thenReturn(true);
        when(attributeValueRepository.existsAttributeValueByIdAndAttribute(2L, testAttribute)).thenReturn(true);

        // Act
        attributeValueService.manageAttributeValues(testAttribute, requests);

        // Assert
        verify(attributeValueRepository).findByAttribute(testAttribute);
        verify(attributeValueRepository).saveAll(attributeValuesCaptor.capture());

        List<AttributeValue> savedValues = attributeValuesCaptor.getValue();
        assertEquals(3, savedValues.size());

        // Verify the values are correctly set up for saving
        Map<Long, String> expectedValues = new HashMap<>();
        expectedValues.put(1L, "Red"); // kept as is
        expectedValues.put(2L, "Navy Blue"); // updated
        // The new value won't have an ID yet

        for (AttributeValue value : savedValues) {
            if (value.getId() != null) {
                assertEquals(expectedValues.get(value.getId()), value.getValue());
            } else {
                assertEquals("Green", value.getValue());
            }
        }

        verify(attributeValueRepository).deleteAll(List.of(valueToDelete));
    }

    @Test
    void manageAttributeValues_WithNullAttribute_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeValueService.manageAttributeValues(null, List.of(testValueRequest1)));
        verify(attributeValueRepository, never()).findByAttribute(any());
        verify(attributeValueRepository, never()).saveAll(any());
        verify(attributeValueRepository, never()).deleteAll(any());
    }

    @Test
    void manageAttributeValues_WithNullRequestsList_DoesNothing() {
        // Act
        attributeValueService.manageAttributeValues(testAttribute, null);

        // Assert
        verify(attributeValueRepository, never()).findByAttribute(any());
        verify(attributeValueRepository, never()).saveAll(any());
        verify(attributeValueRepository, never()).deleteAll(any());
    }

    @Test
    void manageAttributeValues_WithEmptyRequestsList_DoesNothing() {
        // Act
        attributeValueService.manageAttributeValues(testAttribute, Collections.emptyList());

        // Assert
        verify(attributeValueRepository, never()).findByAttribute(any());
        verify(attributeValueRepository, never()).saveAll(any());
        verify(attributeValueRepository, never()).deleteAll(any());
    }
}