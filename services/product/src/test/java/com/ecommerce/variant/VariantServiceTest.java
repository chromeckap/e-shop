package com.ecommerce.variant;

import com.ecommerce.attributevalue.AttributeValueService;
import com.ecommerce.exception.VariantNotFoundException;
import com.ecommerce.product.Product;
import com.ecommerce.product.ProductService;
import com.ecommerce.variant.purchase.CartItemRequest;
import com.ecommerce.variant.purchase.PurchaseResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VariantServiceTest {

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private VariantMapper variantMapper;

    @Mock
    private VariantValidator variantValidator;

    @Mock
    private AttributeValueService attributeValueService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private VariantService variantService;

    @Test
    @DisplayName("Find Variant Entity By ID - Success")
    void findVariantEntityById_Success() {
        // Arrange
        Long variantId = 1L;
        Variant mockVariant = new Variant();
        mockVariant.setId(variantId);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(mockVariant));

        // Act
        Variant result = variantService.findVariantEntityById(variantId);

        // Assert
        assertNotNull(result);
        assertEquals(variantId, result.getId());
        verify(variantRepository).findById(variantId);
    }

    @Test
    @DisplayName("Find Variant Entity By ID - Not Found")
    void findVariantEntityById_NotFound() {
        // Arrange
        Long variantId = 1L;
        when(variantRepository.findById(variantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VariantNotFoundException.class,
                () -> variantService.findVariantEntityById(variantId));
    }

    @Test
    @DisplayName("Get Variant By ID - Success")
    void getVariantById_Success() {
        // Arrange
        Long variantId = 1L;
        Variant mockVariant = new Variant();
        VariantResponse mockResponse = new VariantResponse(
                1L,
                "SKU-001",
                BigDecimal.TEN,
                BigDecimal.TEN,
                1,
                false,
                null
        );

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(mockVariant));
        when(variantMapper.toResponse(mockVariant)).thenReturn(mockResponse);

        // Act
        VariantResponse result = variantService.getVariantById(variantId);

        // Assert
        assertNotNull(result);
        assertEquals(variantId, result.id());
        verify(variantMapper).toResponse(mockVariant);
    }

    @Test
    @DisplayName("Create Variant - Success")
    void createVariant_Success() {
        // Arrange
        VariantRequest request = new VariantRequest(
                1L,
                1L,
                "SKU-001",
                BigDecimal.TEN,
                BigDecimal.TEN,
                1,
                false,
                null
        );

        Variant mockVariant = new Variant();
        Product mockProduct = new Product();

        when(productService.findProductEntityById(request.productId())).thenReturn(mockProduct);
        when(variantMapper.toVariant(request)).thenReturn(mockVariant);
        when(variantRepository.save(any(Variant.class))).thenAnswer(invocation -> {
            Variant savedVariant = invocation.getArgument(0);
            savedVariant.setId(1L);
            return savedVariant;
        });

        // Act
        Long result = variantService.createVariant(request);

        // Assert
        assertNotNull(result);
        verify(variantValidator).validateProductNumberVariants(mockVariant);
        verify(variantRepository).save(mockVariant);
    }

    @Test
    @DisplayName("Update Variant - Success")
    void updateVariant_Success() {
        // Arrange
        Long variantId = 1L;
        VariantRequest request = new VariantRequest(
                1L,
                1L,
                "SKU-001",
                BigDecimal.TEN,
                BigDecimal.TEN,
                1,
                false,
                null
        );

        Variant existingVariant = new Variant();
        existingVariant.setId(variantId);

        Variant updatedVariant = new Variant();
        updatedVariant.setId(variantId);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(existingVariant));
        when(variantMapper.toVariant(request)).thenReturn(updatedVariant);
        when(variantRepository.save(any(Variant.class))).thenAnswer(invocation -> {
            Variant savedVariant = invocation.getArgument(0);
            savedVariant.setId(variantId);
            return savedVariant;
        });

        // Act
        Long result = variantService.updateVariant(variantId, request);

        // Assert
        assertNotNull(result);
        assertEquals(variantId, result);
        verify(variantRepository).save(updatedVariant);
    }

    @Test
    @DisplayName("Delete Variant By ID - Success")
    void deleteVariantById_Success() {
        // Arrange
        Long variantId = 1L;
        Variant mockVariant = new Variant();

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(mockVariant));

        // Act
        variantService.deleteVariantById(variantId);

        // Assert
        verify(variantRepository).delete(mockVariant);
    }

    @Test
    @DisplayName("Delete Variant By ID - Not Found")
    void deleteVariantById_NotFound() {
        // Arrange
        Long variantId = 1L;
        when(variantRepository.findById(variantId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(VariantNotFoundException.class,
                () -> variantService.deleteVariantById(variantId));
    }

    @Test
    @DisplayName("Get Variants By Cart Items - Success")
    void getVariantsByCartItems_Success() {
        // Arrange
        List<CartItemRequest> cartItems = List.of(
                new CartItemRequest(1L, 2),
                new CartItemRequest(2L, 3)
        );

        Variant variant1 = new Variant();
        variant1.setId(1L);
        Variant variant2 = new Variant();
        variant2.setId(2L);

        PurchaseResponse response1 = new PurchaseResponse(1L, 1L, "1st", "/", BigDecimal.TEN, 2, 2, true, BigDecimal.TEN, null);
        PurchaseResponse response2 = new PurchaseResponse(2L, 2L, "1st", "/", BigDecimal.TEN, 2, 2, true, BigDecimal.TEN, null);

        when(variantRepository.findAllById(anyList())).thenReturn(List.of(variant1, variant2));
        when(variantMapper.toPurchaseResponse(variant1, 2)).thenReturn(response1);
        when(variantMapper.toPurchaseResponse(variant2, 3)).thenReturn(response2);

        // Act
        List<PurchaseResponse> result = variantService.getVariantsByCartItems(cartItems);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(variantRepository).findAllById(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Get Variants By Cart Items - Partial Match")
    void getVariantsByCartItems_PartialMatch() {
        // Arrange
        List<CartItemRequest> cartItems = List.of(
                new CartItemRequest(1L, 2),
                new CartItemRequest(999L, 3)  // Non-existent variant
        );

        Variant variant1 = new Variant();
        variant1.setId(1L);

        PurchaseResponse response1 = new PurchaseResponse(1L, 1L, "1st", "/", BigDecimal.TEN, 2, 2, true, BigDecimal.TEN, null);

        when(variantRepository.findAllById(anyList())).thenReturn(List.of(variant1));
        when(variantMapper.toPurchaseResponse(variant1, 2)).thenReturn(response1);

        // Act
        List<PurchaseResponse> result = variantService.getVariantsByCartItems(cartItems);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().variantId());
    }
}