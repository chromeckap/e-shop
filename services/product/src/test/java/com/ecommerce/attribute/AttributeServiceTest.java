package com.ecommerce.attribute;

import com.ecommerce.attributevalue.AttributeValue;
import com.ecommerce.attributevalue.AttributeValueService;
import com.ecommerce.exception.AttributeNotFoundException;
import com.ecommerce.product.Product;
import com.ecommerce.product.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeServiceTest {

    @Mock
    private AttributeRepository attributeRepository;

    @Mock
    private AttributeMapper attributeMapper;

    @Mock
    private AttributeValidator attributeValidator;

    @Mock
    private AttributeValueService attributeValueService;

    @InjectMocks
    private AttributeService attributeService;

    private Attribute testAttribute;
    private AttributeResponse testAttributeResponse;
    private AttributeRequest testAttributeRequest;

    @BeforeEach
    void setUp() {
        testAttribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .values(new ArrayList<>())
                .build();

        testAttributeResponse = AttributeResponse.builder()
                .id(1L)
                .name("Color")
                .values(Collections.emptyList())
                .build();

        testAttributeRequest = new AttributeRequest(
                1L,
                "Color",
                Collections.emptyList()
        );
    }

    @Test
    void findAttributeEntityById_WithValidId_ReturnsAttribute() {
        // Arrange
        when(attributeRepository.findById(1L)).thenReturn(Optional.of(testAttribute));

        // Act
        Attribute result = attributeService.findAttributeEntityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testAttribute.getId(), result.getId());
        assertEquals(testAttribute.getName(), result.getName());
        verify(attributeRepository).findById(1L);
    }

    @Test
    void findAttributeEntityById_WithInvalidId_ThrowsAttributeNotFoundException() {
        // Arrange
        when(attributeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AttributeNotFoundException.class,
                () -> attributeService.findAttributeEntityById(999L));
        verify(attributeRepository).findById(999L);
    }

    @Test
    void findAttributeEntityById_WithNullId_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeService.findAttributeEntityById(null));
        verify(attributeRepository, never()).findById(any());
    }

    @Test
    void processProductAttributes_WithValidRequest_ReturnsAttributes() {
        // Arrange
        Set<Long> attributeIds = Set.of(1L, 2L);
        List<Attribute> attributes = List.of(
                testAttribute,
                Attribute.builder().id(2L).name("Size").build()
        );
        ProductRequest request = mock(ProductRequest.class);
        when(request.attributeIds()).thenReturn(attributeIds);
        when(attributeRepository.findAllById(attributeIds)).thenReturn(attributes);

        // Act
        Set<Attribute> result = attributeService.processProductAttributes(request);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(attributeValidator).validateAllAttributesExist(attributeIds);
        verify(attributeRepository).findAllById(attributeIds);
    }

    @Test
    void processProductAttributes_WithEmptyAttributeIds_ReturnsEmptySet() {
        // Arrange
        ProductRequest request = mock(ProductRequest.class);
        when(request.attributeIds()).thenReturn(Collections.emptySet());

        // Act
        Set<Attribute> result = attributeService.processProductAttributes(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(attributeValidator, never()).validateAllAttributesExist(any());
        verify(attributeRepository, never()).findAllById(any());
    }

    @Test
    void processProductAttributes_WithNullAttributeIds_ReturnsEmptySet() {
        // Arrange
        ProductRequest request = mock(ProductRequest.class);
        when(request.attributeIds()).thenReturn(null);

        // Act
        Set<Attribute> result = attributeService.processProductAttributes(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(attributeValidator, never()).validateAllAttributesExist(any());
        verify(attributeRepository, never()).findAllById(any());
    }

    @Test
    void processProductAttributes_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeService.processProductAttributes(null));
        verify(attributeValidator, never()).validateAllAttributesExist(any());
        verify(attributeRepository, never()).findAllById(any());
    }

    @Test
    void getAttributeById_WithValidId_ReturnsAttributeResponse() {
        // Arrange
        when(attributeRepository.findById(1L)).thenReturn(Optional.of(testAttribute));
        when(attributeMapper.toResponse(testAttribute)).thenReturn(testAttributeResponse);

        // Act
        AttributeResponse result = attributeService.getAttributeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testAttributeResponse.id(), result.id());
        assertEquals(testAttributeResponse.name(), result.name());
        verify(attributeRepository).findById(1L);
        verify(attributeMapper).toResponse(testAttribute);
    }

    @Test
    void getAllAttributes_ReturnsAllAttributeResponses() {
        // Arrange
        List<Attribute> attributes = List.of(testAttribute);
        when(attributeRepository.findAll()).thenReturn(attributes);
        when(attributeMapper.toResponse(testAttribute)).thenReturn(testAttributeResponse);

        // Act
        Set<AttributeResponse> result = attributeService.getAllAttributes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(testAttributeResponse));
        verify(attributeRepository).findAll();
        verify(attributeMapper).toResponse(testAttribute);
    }

    @Test
    void createAttribute_WithValidRequest_ReturnsAttributeId() {
        // Arrange
        when(attributeMapper.toAttribute(testAttributeRequest)).thenReturn(testAttribute);
        when(attributeRepository.save(testAttribute)).thenReturn(testAttribute);

        // Act
        Long result = attributeService.createAttribute(testAttributeRequest);

        // Assert
        assertEquals(testAttribute.getId(), result);
        verify(attributeMapper).toAttribute(testAttributeRequest);
        verify(attributeRepository).save(testAttribute);
        verify(attributeValueService).manageAttributeValues(testAttribute, testAttributeRequest.values());
    }

    @Test
    void createAttribute_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeService.createAttribute(null));
        verify(attributeMapper, never()).toAttribute(any());
        verify(attributeRepository, never()).save(any());
        verify(attributeValueService, never()).manageAttributeValues(any(), any());
    }

    @Test
    void updateAttribute_WithValidIdAndRequest_ReturnsAttributeId() {
        // Arrange
        Attribute updatedAttribute = Attribute.builder()
                .id(1L)
                .name("Updated Color")
                .build();

        when(attributeRepository.findById(1L)).thenReturn(Optional.of(testAttribute));
        when(attributeMapper.toAttribute(testAttributeRequest)).thenReturn(updatedAttribute);
        when(attributeRepository.save(updatedAttribute)).thenReturn(updatedAttribute);

        // Act
        Long result = attributeService.updateAttribute(1L, testAttributeRequest);

        // Assert
        assertEquals(updatedAttribute.getId(), result);
        verify(attributeRepository).findById(1L);
        verify(attributeMapper).toAttribute(testAttributeRequest);
        verify(attributeRepository).save(updatedAttribute);
        verify(attributeValueService).manageAttributeValues(updatedAttribute, testAttributeRequest.values());
    }

    @Test
    void updateAttribute_WithNullId_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeService.updateAttribute(null, testAttributeRequest));
        verify(attributeRepository, never()).findById(any());
        verify(attributeMapper, never()).toAttribute(any());
        verify(attributeRepository, never()).save(any());
        verify(attributeValueService, never()).manageAttributeValues(any(), any());
    }

    @Test
    void updateAttribute_WithNullRequest_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeService.updateAttribute(1L, null));
        verify(attributeRepository, never()).findById(any());
        verify(attributeMapper, never()).toAttribute(any());
        verify(attributeRepository, never()).save(any());
        verify(attributeValueService, never()).manageAttributeValues(any(), any());
    }

    @Test
    void deleteAttributeById_WithValidId_DeletesAttribute() {
        // Arrange
        when(attributeRepository.findById(1L)).thenReturn(Optional.of(testAttribute));

        // Act
        attributeService.deleteAttributeById(1L);

        // Assert
        verify(attributeRepository).findById(1L);
        verify(attributeRepository).delete(testAttribute);
    }

    @Test
    void deleteAttributeById_WithNullId_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> attributeService.deleteAttributeById(null));
        verify(attributeRepository, never()).findById(any());
        verify(attributeRepository, never()).delete(any());
    }

    @Test
    void getAttributesByProducts_WithValidProducts_ReturnsAttributeResponses() {
        // Arrange
        Product product = new Product();
        product.setAttributes(Set.of(testAttribute));

        when(attributeMapper.toResponse(testAttribute)).thenReturn(testAttributeResponse);

        // Act
        Set<AttributeResponse> result = attributeService.getAttributesByProducts(List.of(product));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(testAttributeResponse));
        verify(attributeMapper).toResponse(testAttribute);
    }

    @Test
    void getAttributesByProducts_WithEmptyProducts_ReturnsEmptySet() {
        // Act
        Set<AttributeResponse> result = attributeService.getAttributesByProducts(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(attributeMapper, never()).toResponse(any());
    }
}