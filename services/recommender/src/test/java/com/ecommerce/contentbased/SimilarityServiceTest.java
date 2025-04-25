package com.ecommerce.contentbased;

import com.ecommerce.feignclient.product.ProductOverviewResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimilarityServiceTest {

    private SimilarityService similarityService;

    @BeforeEach
    public void setUp() {
        similarityService = new SimilarityService();
    }

    @Test
    public void testCalculateTextSimilarity_ExactMatch() {
        String text = "High-quality wireless bluetooth headphones";
        double similarity = similarityService.calculateTextSimilarity(text, text);
        assertEquals(1.0, similarity, 0.001);
    }

    @Test
    public void testCalculateTextSimilarity_PartialMatch() {
        String text1 = "High-quality wireless bluetooth headphones";
        String text2 = "wireless bluetooth earbuds premium";
        double similarity = similarityService.calculateTextSimilarity(text1, text2);
        assertTrue(similarity > 0 && similarity < 1.0);
    }

    @Test
    public void testCalculateTextSimilarity_NoMatch() {
        String text1 = "laptop computer";
        String text2 = "kitchen appliance";
        double similarity = similarityService.calculateTextSimilarity(text1, text2);
        assertEquals(0.0, similarity, 0.001);
    }

    @Test
    public void testCalculateTextSimilarity_NullInputs() {
        double similarity = similarityService.calculateTextSimilarity(null, "test");
        assertEquals(0.0, similarity, 0.001);

        similarity = similarityService.calculateTextSimilarity("test", null);
        assertEquals(0.0, similarity, 0.001);
    }

    @Test
    public void testCalculateCategorySimilarity_FullMatch() {
        Set<Long> categories1 = new HashSet<>(Set.of(1L, 2L, 3L));
        Set<Long> categories2 = new HashSet<>(Set.of(1L, 2L, 3L));
        double similarity = similarityService.calculateCategorySimilarity(categories1, categories2);
        assertEquals(1.0, similarity, 0.001);
    }

    @Test
    public void testCalculateCategorySimilarity_PartialMatch() {
        Set<Long> categories1 = new HashSet<>(Set.of(1L, 2L, 3L));
        Set<Long> categories2 = new HashSet<>(Set.of(2L, 3L, 4L));
        double similarity = similarityService.calculateCategorySimilarity(categories1, categories2);
        assertTrue(similarity > 0 && similarity < 1.0);
    }

    @Test
    public void testCalculateCategorySimilarity_NoMatch() {
        Set<Long> categories1 = new HashSet<>(Set.of(1L, 2L, 3L));
        Set<Long> categories2 = new HashSet<>(Set.of(4L, 5L, 6L));
        double similarity = similarityService.calculateCategorySimilarity(categories1, categories2);
        assertEquals(0.0, similarity, 0.001);
    }

    @Test
    public void testCalculateCategorySimilarity_EmptyInputs() {
        Set<Long> categories1 = new HashSet<>();
        Set<Long> categories2 = new HashSet<>(Set.of(1L, 2L));
        double similarity = similarityService.calculateCategorySimilarity(categories1, categories2);
        assertEquals(0.0, similarity, 0.001);
    }

    @Test
    public void testCalculatePriceSimilarity_SamePrice() {
        BigDecimal price = BigDecimal.valueOf(100.00);
        double similarity = similarityService.calculatePriceSimilarity(price, price);
        assertEquals(1.0, similarity, 0.001);
    }

    @Test
    public void testCalculatePriceSimilarity_Closeprices() {
        BigDecimal price1 = BigDecimal.valueOf(100.00);
        BigDecimal price2 = BigDecimal.valueOf(90.00);
        double similarity = similarityService.calculatePriceSimilarity(price1, price2);
        assertTrue(similarity > 0.8 && similarity < 1.0);
    }

    @Test
    public void testCalculatePriceSimilarity_DistantPrices() {
        BigDecimal price1 = BigDecimal.valueOf(100.00);
        BigDecimal price2 = BigDecimal.valueOf(10.00);
        double similarity = similarityService.calculatePriceSimilarity(price1, price2);
        assertTrue(similarity >= 0 && similarity < 0.5);
    }

    @Test
    public void testCalculatePriceSimilarity_NullInputs() {
        double similarity = similarityService.calculatePriceSimilarity(null, BigDecimal.TEN);
        assertEquals(0.0, similarity, 0.001);

        similarity = similarityService.calculatePriceSimilarity(BigDecimal.TEN, null);
        assertEquals(0.0, similarity, 0.001);
    }

    @Test
    public void testCalculatePriceSimilarity_ZeroPrices() {
        double similarity = similarityService.calculatePriceSimilarity(BigDecimal.ZERO, BigDecimal.TEN);
        assertEquals(0.0, similarity, 0.001);

        similarity = similarityService.calculatePriceSimilarity(BigDecimal.TEN, BigDecimal.ZERO);
        assertEquals(0.0, similarity, 0.001);
    }

    @Test
    public void testCalculateSimilarity_OverallSimilarity() {
        ProductOverviewResponse product1 = createProductOverviewResponse(
                1L,
                "Wireless Bluetooth Headphones",
                "High-quality noise-cancelling headphones",
                BigDecimal.valueOf(199.99),
                new HashSet<>(Set.of(1L, 2L))
        );

        ProductOverviewResponse product2 = createProductOverviewResponse(
                2L,
                "Wireless Bluetooth Earbuds",
                "Premium noise-cancelling earbuds",
                BigDecimal.valueOf(179.99),
                new HashSet<>(Set.of(1L, 3L))
        );

        double similarity = similarityService.calculateSimilarity(product1, product2);
        assertTrue(similarity > 0 && similarity < 1.0);
    }

    // Helper method to create ProductOverviewResponse
    private ProductOverviewResponse createProductOverviewResponse(
            Long id, String name, String description, BigDecimal price, Set<Long> categoryIds
    ) {
        return new ProductOverviewResponse(
                id,
                name,
                description,
                price,
                price,
                true,
                true,
                categoryIds,
                new ArrayList<>(),
                "/"
        );
    }
}