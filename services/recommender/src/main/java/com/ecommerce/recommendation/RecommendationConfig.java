package com.ecommerce.recommendation;

import com.ecommerce.contentbased.ContentBasedService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class RecommendationConfig {
    private final ContentBasedService recommenderService;

    @PostConstruct
    public void initRecommendations() {
        recommenderService.refreshRecommendations();
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduledRefreshRecommendations() {
        recommenderService.refreshRecommendations();
    }
}
