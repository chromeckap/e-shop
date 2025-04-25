package com.ecommerce.product;

import com.ecommerce.variant.Variant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class ProductPriceService {

    public BigDecimal getCheapestVariantPrice(Product product) {
        return product.getVariants().stream()
                .min(Comparator.comparing(Variant::getPrice))
                .map(Variant::getPrice)
                .orElse(null);
    }

    public BigDecimal getCheapestVariantPrice(List<Product> products) {
        return products.stream()
                .flatMap(product -> product.getVariants().stream())
                .min(Comparator.comparing(Variant::getPrice))
                .map(Variant::getPrice)
                .orElse(null);
    }

    public BigDecimal getHighestVariantPrice(List<Product> products) {
        return products.stream()
                .flatMap(product -> product.getVariants().stream())
                .max(Comparator.comparing(Variant::getPrice))
                .map(Variant::getPrice)
                .orElse(null);
    }


    public BigDecimal getCheapestVariantBasePrice(Product product) {
        return product.getVariants().stream()
                .min(Comparator.comparing(Variant::getPrice))
                .map(Variant::getBasePrice)
                .orElse(null);
    }

    public boolean isVariantsPricesEqual(Product product) {
        return product.getVariants().stream()
                .map(Variant::getPrice)
                .distinct()
                .count() == 1;
    }
}
