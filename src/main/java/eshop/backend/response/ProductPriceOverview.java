package eshop.backend.response;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Getter
public class ProductPriceOverview {
    private final BigDecimal basicPrice;
    private final BigDecimal discountedPrice;
    private final int discountPercentage;
    private final boolean isVariantsPricesEqual;

    public ProductPriceOverview(BigDecimal basicPrice, BigDecimal discountedPrice, boolean isVariantsPricesEqual) {
        this.basicPrice = basicPrice;
        this.discountedPrice = discountedPrice;
        this.discountPercentage = this.calculateDiscountPercentage();
        this.isVariantsPricesEqual = isVariantsPricesEqual;
    }

    private int calculateDiscountPercentage() {
        if (Objects.equals(basicPrice, discountedPrice)) {
            return 0;
        }
        BigDecimal discount = basicPrice.divide(discountedPrice, RoundingMode.HALF_UP);
        return discount.multiply(new BigDecimal(100)).intValue();
    }
}
