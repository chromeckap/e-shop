package com.ecommerce.recommendation;

import com.ecommerce.contentbased.ContentBasedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private ContentBasedService contentBasedService;

    private RecommendationService recommendationService;

    @BeforeEach
    public void setUp() {
        recommendationService = new RecommendationService(contentBasedService);
    }

    @Test
    public void testRefreshRecommendations() {
        // Perform the test
        recommendationService.refreshRecommendations();

        // Verify interactions
        verify(contentBasedService).refreshRecommendations();
    }
}
