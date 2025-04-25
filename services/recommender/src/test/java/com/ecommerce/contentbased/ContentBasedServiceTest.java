package com.ecommerce.contentbased;

import com.ecommerce.feignclient.product.ProductClient;
import com.ecommerce.feignclient.product.ProductOverviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContentBasedServiceTest {

    @Mock
    private SimilarityService similarityService;

    @Mock
    private ProductClient productClient;

    private ContentBasedService contentBasedService;

    @BeforeEach
    public void setUp() {
        contentBasedService = new ContentBasedService(similarityService, productClient);
    }

    @Test
    public void testGetRecommendations_ProductNotExists() {
        // Prepare test data
        Long productId = 999L;

        // Perform the test
        List<ProductOverviewResponse> recommendations = contentBasedService.getRecommendations(productId, 5);

        // Assertions
        assertTrue(recommendations.isEmpty());
    }

    @Test
    public void testFindSimilarProducts() {
        // Prepare test data
        ProductOverviewResponse targetProduct = createProductOverviewResponse(
                1L, "Target Product", "Target Description", BigDecimal.valueOf(50),
                new HashSet<>(Arrays.asList(1L, 2L))
        );

        ProductOverviewResponse similarProduct1 = createProductOverviewResponse(
                2L, "Similar Product 1", "Similar Description", BigDecimal.valueOf(55),
                new HashSet<>(Arrays.asList(2L, 3L))
        );

        ProductOverviewResponse similarProduct2 = createProductOverviewResponse(
                3L, "Similar Product 2", "Another Description", BigDecimal.valueOf(45),
                new HashSet<>(Arrays.asList(3L, 4L))
        );

        List<ProductOverviewResponse> allProducts = Arrays.asList(targetProduct, similarProduct1, similarProduct2);

        // Mock similarity calculations
        when(similarityService.calculateSimilarity(eq(targetProduct), eq(similarProduct1)))
                .thenReturn(0.75);
        when(similarityService.calculateSimilarity(eq(targetProduct), eq(similarProduct2)))
                .thenReturn(0.3);

        // Use reflection to call the private method
        List<ProductSimilarity> similarities = contentBasedService.findSimilarProducts(targetProduct, allProducts);

        // Assertions
        assertNotNull(similarities);
        assertEquals(2, similarities.size());
        assertEquals(similarProduct1.id(), similarities.get(0).productId());
        assertEquals(0.75, similarities.get(0).similarity(), 0.01);
        assertEquals(similarProduct2.id(), similarities.get(1).productId());
        assertEquals(0.3, similarities.get(1).similarity(), 0.01);
    }

    // Helper method to create ProductOverviewResponse with safe related product ids
    private ProductOverviewResponse createProductOverviewResponse(
            Long id, String name, String description, BigDecimal price, Set<Long> categoryIds
    ) {
        return createProductOverviewResponse(
                id,
                name,
                description,
                price,
                categoryIds,
                new ArrayList<>() // related product ids
        );
    }

    // Detailed method to create ProductOverviewResponse with all fields
    private ProductOverviewResponse createProductOverviewResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Set<Long> categoryIds,
            List<Long> relatedProductIds
    ) {
        return new ProductOverviewResponse(
                id,
                name,
                description,
                price,
                price, // Assuming duplicate price param based on error in original code
                true,  // Assuming some boolean param
                true,  // Assuming another boolean param
                categoryIds,
                relatedProductIds,
                null   // Assuming last param might be null
        );
    }
}