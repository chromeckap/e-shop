package com.ecommerce.contentbased;

import com.ecommerce.feignclient.product.ProductOverviewResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SimilarityService {
    private static final double NAME_WEIGHT = 0.2;
    private static final double DESCRIPTION_WEIGHT = 0.15;
    private static final double CATEGORY_WEIGHT = 0.4;
    private static final double PRICE_WEIGHT = 0.25;

    public double calculateSimilarity(ProductOverviewResponse product1, ProductOverviewResponse product2) {
        double nameSimilarity = this.calculateTextSimilarity(product1.name(), product2.name());
        double descriptionSimilarity = this.calculateTextSimilarity(product1.description(), product2.description());
        double categorySimilarity = this.calculateCategorySimilarity(product1.categoryIds(), product2.categoryIds());
        double priceSimilarity = this.calculatePriceSimilarity(product1.price(), product2.price());

        return (nameSimilarity * NAME_WEIGHT) +
                (descriptionSimilarity * DESCRIPTION_WEIGHT) +
                (categorySimilarity * CATEGORY_WEIGHT) +
                (priceSimilarity * PRICE_WEIGHT);
    }

    double calculateTextSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null)
            return 0.0;

        Set<String> words1 = Arrays.stream(text1.toLowerCase().split("\\W+"))
                .filter(word -> word.length() > 2).collect(Collectors.toSet());

        Set<String> words2 = Arrays.stream(text2.toLowerCase().split("\\W+"))
                .filter(word -> word.length() > 2).collect(Collectors.toSet());

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    double calculateCategorySimilarity(Set<Long> categories1, Set<Long> categories2) {
        if (categories1.isEmpty() || categories2.isEmpty())
            return 0.0;

        Set<Long> intersection = new HashSet<>(categories1);
        intersection.retainAll(categories2);

        return (double) intersection.size() / (categories1.size() + categories2.size() - intersection.size());
    }

    double calculatePriceSimilarity(BigDecimal price1, BigDecimal price2) {
        if (price1 == null || price2 == null ||
                price1.compareTo(BigDecimal.ZERO) <= 0 ||
                price2.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        BigDecimal min = price1.min(price2);
        BigDecimal max = price1.max(price2);
        double ratio = min.doubleValue() / max.doubleValue();

        return Math.pow(ratio, 2);
    }
}
