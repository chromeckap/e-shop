package eshop.backend.service.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import eshop.backend.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductSidebarFilter {

    @JsonProperty
    public Map<Attribute, Map<AttributeValue, Long>> listAttributesAndValuesCountByCategory(Category category, List<Product> productPage) {
        // Initialize the attributeValuesMap with all possible attribute-value pairs in the category
        Map<Attribute, Map<AttributeValue, Long>> attributeValuesMap = category.getProducts().stream()
                .flatMap(product -> product.getAttributes().stream()
                        .flatMap(attribute -> attribute.getValues().stream()
                                .map(attributeValue -> new AbstractMap.SimpleEntry<>(attribute, attributeValue))))
                .collect(Collectors.groupingBy(AbstractMap.SimpleEntry::getKey,
                        Collectors.groupingBy(AbstractMap.SimpleEntry::getValue, Collectors.counting())));

        // Update the count of each attribute-value pair based on the products in the current page
        for (Product product : productPage) {
            for (Attribute attribute : product.getAttributes()) {
                Map<AttributeValue, Long> valueCounts = attributeValuesMap.get(attribute);
                for (AttributeValue value : attribute.getValues()) {
                    valueCounts.put(value, valueCounts.getOrDefault(value, 0L) + 1);
                }
            }
        }

        return attributeValuesMap;
    }

    public BigDecimal minPrice(Map<Attribute, Map<AttributeValue, Long>> attributeValuesMap) {
        return attributeValuesMap.values().stream()
                .flatMap(valueCounts -> valueCounts.keySet().stream())
                .flatMap(attributeValue -> attributeValue.getVariants().stream())
                .map(Variant::getBasePrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal maxPrice(Map<Attribute, Map<AttributeValue, Long>> attributeValuesMap) {
        return attributeValuesMap.values().stream()
                .flatMap(valueCounts -> valueCounts.keySet().stream())
                .flatMap(attributeValue -> attributeValue.getVariants().stream())
                .map(Variant::getBasePrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}

