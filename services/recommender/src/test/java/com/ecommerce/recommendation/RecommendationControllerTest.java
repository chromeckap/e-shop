package com.ecommerce.recommendation;

import com.ecommerce.contentbased.ContentBasedService;
import com.ecommerce.feignclient.product.ProductOverviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {

    @Mock
    private ContentBasedService contentBasedService;

    @Mock
    private RecommendationService recommendationService;

    private RecommendationController recommendationController;

    @BeforeEach
    public void setUp() {
        recommendationController = new RecommendationController(contentBasedService, recommendationService);
    }

    @Test
    public void testGetRecommendationsByProductId() {
        // Prepare test data
        Long productId = 1L;
        List<ProductOverviewResponse> mockRecommendations = Arrays.asList(
                createProductOverviewResponse(2L, "Product 2"),
                createProductOverviewResponse(3L, "Product 3")
        );

        // Mock the service method
        when(contentBasedService.getRecommendations(productId, 8))
                .thenReturn(mockRecommendations);

        // Perform the test
        ResponseEntity<List<ProductOverviewResponse>> response =
                recommendationController.getRecommendationsByProductId(productId, 8);

        // Assertions
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(2L, response.getBody().get(0).id());
        assertEquals(3L, response.getBody().get(1).id());

        // Verify interactions
        verify(contentBasedService).getRecommendations(productId, 8);
    }

    @Test
    public void testRefreshRecommendations() {
        // Perform the test
        ResponseEntity<Void> response = recommendationController.refreshRecommendations();

        // Assertions
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(recommendationService).refreshRecommendations();
    }

    // Helper method to create ProductOverviewResponse
    private ProductOverviewResponse createProductOverviewResponse(Long id, String name) {
        return new ProductOverviewResponse(
                id,
                name,
                "Description",
                BigDecimal.TEN,
                BigDecimal.TEN,
                true,
                true,
                new HashSet<>(),
                new ArrayList<>(),
                "/"
        );
    }
}