package eshop.backend.response;

import eshop.backend.model.Product;
import lombok.Data;

@Data
public class ProductOverviewResponse {
    private Long productId;
    private String name;
    private String description;
    private ProductPriceOverview priceOverview;
    private RatingSummary ratingSummary;

    public ProductOverviewResponse(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
    }
}
