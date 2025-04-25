package com.ecommerce.recommendation;

import com.ecommerce.contentbased.ContentBasedService;
import com.ecommerce.feignclient.product.ProductOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final ContentBasedService contentBasedService;

    public void refreshRecommendations() {
        contentBasedService.refreshRecommendations();
    }
}
