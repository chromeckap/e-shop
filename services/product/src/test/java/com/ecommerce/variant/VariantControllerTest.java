package com.ecommerce.variant;

import com.ecommerce.exception.VariantNotFoundException;
import com.ecommerce.variant.purchase.CartItemRequest;
import com.ecommerce.variant.purchase.PurchaseRequest;
import com.ecommerce.variant.purchase.PurchaseResponse;
import com.ecommerce.variant.purchase.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VariantControllerTest {

    @Mock
    private VariantService variantService;

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private VariantController variantController;

    private VariantResponse testVariantResponse;
    private VariantRequest testVariantRequest;
    private Long testVariantId;
    private Long testProductId;
    private PurchaseResponse testPurchaseResponse;

    @BeforeEach
    void setUp() {
        testVariantId = 1L;
        testProductId = 1L;

        testVariantResponse = VariantResponse.builder()
                .id(testVariantId)
                .sku("TEST-SKU")
                .basePrice(new BigDecimal("100.00"))
                .discountedPrice(new BigDecimal("80.00"))
                .quantity(10)
                .quantityUnlimited(false)
                .attributeValues(null)
                .build();

        testVariantRequest = new VariantRequest(
                testVariantId,
                testProductId,
                "TEST-SKU",
                new BigDecimal("100.00"),
                new BigDecimal("80.00"),
                10,
                false,
                Set.of(1L, 2L)
        );

        testPurchaseResponse = PurchaseResponse.builder()
                .productId(testProductId)
                .variantId(testVariantId)
                .name("Test Product")
                .price(new BigDecimal("80.00"))
                .quantity(2)
                .availableQuantity(10)
                .isAvailable(true)
                .totalPrice(new BigDecimal("160.00"))
                .build();
    }

    @Test
    void getVariantById_WithValidId_ReturnsOkWithVariant() {
        // Arrange
        when(variantService.getVariantById(testVariantId)).thenReturn(testVariantResponse);

        // Act
        ResponseEntity<VariantResponse> response = variantController.getVariantById(testVariantId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testVariantResponse, response.getBody());
        verify(variantService).getVariantById(testVariantId);
    }

    @Test
    void getVariantById_WithInvalidId_ThrowsVariantNotFoundException() {
        // Arrange
        when(variantService.getVariantById(999L)).thenThrow(new VariantNotFoundException("Variant not found"));

        // Act & Assert
        assertThrows(VariantNotFoundException.class, () -> variantController.getVariantById(999L));
        verify(variantService).getVariantById(999L);
    }

    @Test
    void getVariantsByProductId_WithValidId_ReturnsOkWithVariants() {
        // Arrange
        Set<VariantResponse> variants = Set.of(testVariantResponse);
        when(variantService.getVariantsByProductId(testProductId)).thenReturn(variants);

        // Act
        ResponseEntity<Set<VariantResponse>> response = variantController.getVariantsByProductId(testProductId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(variants, response.getBody());
        verify(variantService).getVariantsByProductId(testProductId);
    }

    @Test
    void createVariant_WithValidRequest_ReturnsCreatedWithId() {
        // Arrange
        when(variantService.createVariant(testVariantRequest)).thenReturn(testVariantId);

        // Act
        ResponseEntity<Long> response = variantController.createVariant(testVariantRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testVariantId, response.getBody());
        verify(variantService).createVariant(testVariantRequest);
    }

    @Test
    void updateVariant_WithValidIdAndRequest_ReturnsOkWithId() {
        // Arrange
        when(variantService.updateVariant(testVariantId, testVariantRequest)).thenReturn(testVariantId);

        // Act
        ResponseEntity<Long> response = variantController.updateVariant(testVariantId, testVariantRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testVariantId, response.getBody());
        verify(variantService).updateVariant(testVariantId, testVariantRequest);
    }

    @Test
    void deleteVariantById_WithValidId_ReturnsNoContent() {
        // Arrange
        doNothing().when(variantService).deleteVariantById(testVariantId);

        // Act
        ResponseEntity<Variant> response = variantController.deleteVariantById(testVariantId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(variantService).deleteVariantById(testVariantId);
    }

    @Test
    void getVariantsByCartItems_WithValidItems_ReturnsOkWithPurchaseResponses() {
        // Arrange
        List<CartItemRequest> cartItems = List.of(
                new CartItemRequest(1L, 2),
                new CartItemRequest(2L, 3)
        );

        List<PurchaseResponse> purchaseResponses = List.of(testPurchaseResponse);
        when(variantService.getVariantsByCartItems(cartItems)).thenReturn(purchaseResponses);

        // Act
        ResponseEntity<List<PurchaseResponse>> response = variantController.getVariantsByCartItems(cartItems);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(purchaseResponses, response.getBody());
        verify(variantService).getVariantsByCartItems(cartItems);
    }

    @Test
    void purchaseVariants_WithValidRequests_ReturnsOkWithPurchaseResponses() {
        // Arrange
        Set<PurchaseRequest> purchaseRequests = Set.of(
                new PurchaseRequest(1L, 2),
                new PurchaseRequest(2L, 3)
        );

        Set<PurchaseResponse> purchaseResponses = Set.of(testPurchaseResponse);
        when(purchaseService.purchaseVariants(purchaseRequests)).thenReturn(purchaseResponses);

        // Act
        ResponseEntity<Set<PurchaseResponse>> response = variantController.purchaseVariants(purchaseRequests);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(purchaseResponses, response.getBody());
        verify(purchaseService).purchaseVariants(purchaseRequests);
    }
}