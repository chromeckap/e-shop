package eshop.backend.response;

import eshop.backend.model.Product;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.Set;

@Data
public class ProductResponse {
    private Long productId;
    private String name;
    private String description;

    private ProductPriceOverview priceOverview;
    private Set<VariantResponse> variants;

    private RatingSummary ratingSummary;
    private Set<ReviewResponse> ratings;

    private boolean isSingleVariant;

    private Set<ProductOverviewResponse> relatedProducts;

    public ProductResponse(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
    }
}