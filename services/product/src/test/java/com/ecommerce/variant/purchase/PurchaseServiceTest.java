package com.ecommerce.variant.purchase;

import com.ecommerce.variant.Variant;
import com.ecommerce.variant.VariantMapper;
import com.ecommerce.variant.VariantRepository;
import com.ecommerce.variant.VariantValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private VariantMapper variantMapper;

    @Mock
    private VariantValidator variantValidator;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    @DisplayName("Purchase Variants - Success")
    void purchaseVariants_Success() {
        // Arrange
        PurchaseRequest request1 = new PurchaseRequest(1L, 2);
        PurchaseRequest request2 = new PurchaseRequest(2L, 3);
        Set<PurchaseRequest> purchaseRequests = Set.of(request1, request2);

        Variant variant1 = new Variant();
        variant1.setId(1L);
        variant1.setQuantity(10);
        variant1.setQuantityUnlimited(false);

        Variant variant2 = new Variant();
        variant2.setId(2L);
        variant2.setQuantity(15);
        variant2.setQuantityUnlimited(false);

        PurchaseResponse response1 = new PurchaseResponse(1L, 1L, "response", "/", BigDecimal.TEN, 2, 3, false, BigDecimal.TEN, null);
        PurchaseResponse response2 = new PurchaseResponse(2L, 2L, "response", "/", BigDecimal.TEN, 2, 3, false, BigDecimal.TEN, null);

        // Mock method calls
        doNothing().when(variantValidator).validateSetNotEmpty(purchaseRequests);
        when(variantRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(variant1, variant2));
        doNothing().when(variantValidator).validateAllVariantsExist(Set.of(1L, 2L));
        doNothing().when(variantValidator).validateTotalPriceIsNotOver(
                any(), any(), any());
        doNothing().when(variantValidator).validateAvailableQuantity(variant1, request1);
        doNothing().when(variantValidator).validateAvailableQuantity(variant2, request2);

        when(variantMapper.toPurchaseResponse(variant1, 2))
                .thenReturn(response1);
        when(variantMapper.toPurchaseResponse(variant2, 3))
                .thenReturn(response2);

        // Act
        Set<PurchaseResponse> result = purchaseService.purchaseVariants(purchaseRequests);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify variant quantities are updated
        assertEquals(8, variant1.getQuantity());
        assertEquals(12, variant2.getQuantity());

        // Verify interactions
        verify(variantRepository).saveAll(List.of(variant1, variant2));
    }

    @Test
    @DisplayName("Purchase Variants with Unlimited Quantity")
    void purchaseVariants_UnlimitedQuantity() {
        // Arrange
        PurchaseRequest request1 = new PurchaseRequest(1L, 2);
        Set<PurchaseRequest> purchaseRequests = Set.of(request1);

        Variant variant1 = new Variant();
        variant1.setId(1L);
        variant1.setQuantity(10);
        variant1.setQuantityUnlimited(true);

        PurchaseResponse response1 = new PurchaseResponse(1L, 1L, "1st", "/", BigDecimal.TEN, 2, 2, true, BigDecimal.TEN, null);

        // Mock method calls
        doNothing().when(variantValidator).validateSetNotEmpty(purchaseRequests);
        when(variantRepository.findAllById(Set.of(1L))).thenReturn(List.of(variant1));
        doNothing().when(variantValidator).validateAllVariantsExist(Set.of(1L));
        doNothing().when(variantValidator).validateTotalPriceIsNotOver(
                any(), any(), any());

        when(variantMapper.toPurchaseResponse(variant1, 2))
                .thenReturn(response1);

        // Act
        Set<PurchaseResponse> result = purchaseService.purchaseVariants(purchaseRequests);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        // Verify variant quantity remains unchanged for unlimited quantity
        assertEquals(10, variant1.getQuantity());

        // Verify interactions
        verify(variantRepository).saveAll(List.of(variant1));
    }

    @Test
    @DisplayName("Purchase Variants - Empty Request Set")
    void purchaseVariants_EmptyRequestSet() {
        // Arrange
        Set<PurchaseRequest> emptyRequests = Set.of();

        // Act & Assert
        doThrow(new IllegalArgumentException("Request set cannot be empty"))
                .when(variantValidator).validateSetNotEmpty(emptyRequests);

        assertThrows(IllegalArgumentException.class,
                () -> purchaseService.purchaseVariants(emptyRequests));

        verify(variantValidator).validateSetNotEmpty(emptyRequests);
    }

    @Test
    @DisplayName("Purchase Variants - Total Price Exceeded")
    void purchaseVariants_TotalPriceExceeded() {
        // Arrange
        PurchaseRequest request1 = new PurchaseRequest(1L, 2);
        Set<PurchaseRequest> purchaseRequests = Set.of(request1);

        Variant variant1 = new Variant();
        variant1.setId(1L);
        variant1.setQuantity(10);
        variant1.setQuantityUnlimited(false);

        // Mock method calls
        doNothing().when(variantValidator).validateSetNotEmpty(purchaseRequests);
        when(variantRepository.findAllById(Set.of(1L))).thenReturn(List.of(variant1));
        doNothing().when(variantValidator).validateAllVariantsExist(Set.of(1L));

        doThrow(new IllegalArgumentException("Total price exceeded"))
                .when(variantValidator).validateTotalPriceIsNotOver(
                        any(), any(), any());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> purchaseService.purchaseVariants(purchaseRequests));

        verify(variantValidator).validateTotalPriceIsNotOver(
                any(), any(), any());
    }

    @Test
    @DisplayName("Purchase Variants - Insufficient Quantity")
    void purchaseVariants_InsufficientQuantity() {
        // Arrange
        PurchaseRequest request1 = new PurchaseRequest(1L, 20); // Requesting more than available
        Set<PurchaseRequest> purchaseRequests = Set.of(request1);

        Variant variant1 = new Variant();
        variant1.setId(1L);
        variant1.setQuantity(10);
        variant1.setQuantityUnlimited(false);

        // Mock method calls
        doNothing().when(variantValidator).validateSetNotEmpty(purchaseRequests);
        when(variantRepository.findAllById(Set.of(1L))).thenReturn(List.of(variant1));
        doNothing().when(variantValidator).validateAllVariantsExist(Set.of(1L));
        doNothing().when(variantValidator).validateTotalPriceIsNotOver(
                any(), any(), any());

        doThrow(new IllegalArgumentException("Insufficient quantity"))
                .when(variantValidator).validateAvailableQuantity(variant1, request1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> purchaseService.purchaseVariants(purchaseRequests));

        verify(variantValidator).validateAvailableQuantity(variant1, request1);
    }

    @Test
    @DisplayName("Calculate Updated Quantity - Normal Scenario")
    void calculateUpdatedQuantity_Normal() {
        // Arrange
        Variant variant = new Variant();
        variant.setId(1L);
        variant.setQuantity(10);
        variant.setQuantityUnlimited(false);

        PurchaseRequest request = new PurchaseRequest(1L, 3);

        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = PurchaseService.class
                    .getDeclaredMethod("calculateUpdatedQuantity", Variant.class, PurchaseRequest.class);
            method.setAccessible(true);

            doNothing().when(variantValidator).validateAvailableQuantity(variant, request);

            // Act
            int result = (int) method.invoke(purchaseService, variant, request);

            // Assert
            assertEquals(7, result);
            verify(variantValidator).validateAvailableQuantity(variant, request);
        } catch (Exception e) {
            fail("Failed to test calculateUpdatedQuantity", e);
        }
    }
}