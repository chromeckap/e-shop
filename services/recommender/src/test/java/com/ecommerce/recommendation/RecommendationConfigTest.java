package com.ecommerce.recommendation;

import com.ecommerce.contentbased.ContentBasedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationConfigTest {

    @Mock
    private ContentBasedService recommenderService;

    private RecommendationConfig recommendationConfig;

    @BeforeEach
    public void setUp() {
        recommendationConfig = new RecommendationConfig(recommenderService);
    }

    @Test
    public void testInitRecommendations() {
        // Call the method annotated with @PostConstruct
        recommendationConfig.initRecommendations();

        // Verify that refreshRecommendations is called
        verify(recommenderService).refreshRecommendations();
    }

    @Test
    public void testScheduledRefreshRecommendations() {
        // Call the method annotated with @Scheduled
        recommendationConfig.scheduledRefreshRecommendations();

        // Verify that refreshRecommendations is called
        verify(recommenderService).refreshRecommendations();
    }
}