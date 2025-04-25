package com.ecommerce.contentbased;

import com.ecommerce.feignclient.product.ProductClient;
import com.ecommerce.feignclient.product.ProductOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentBasedService {
    private final SimilarityService similarityService;
    private final ProductClient productClient;

    private final Map<Long, List<ProductSimilarity>> contentBasedMap = new ConcurrentHashMap<>();

    private static final double MIN_SIMILARITY = 0.2;


    public void refreshRecommendations() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        List<ProductOverviewResponse> allProducts = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger page = new AtomicInteger(0);
        AtomicBoolean hasMorePages = new AtomicBoolean(true);

        Runnable fetchTask = () -> {
            if (hasMorePages.get()) {
                Page<ProductOverviewResponse> response = productClient.getAllProducts(page.get(), 10, "ASC", "id");
                allProducts.addAll(response.getContent());
                page.incrementAndGet();
                hasMorePages.set(!response.isLast());
            }
        };

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(fetchTask, 0, 500, TimeUnit.MILLISECONDS);

        while (!future.isDone()) {
            if (!hasMorePages.get()) {
                future.cancel(false);
                scheduler.shutdown();
            }
        }

        contentBasedMap.clear();

        for (ProductOverviewResponse product : allProducts) {
            List<ProductSimilarity> similarities = this.findSimilarProducts(product, allProducts);
            contentBasedMap.put(product.id(), similarities);
        }
    }

    public List<ProductOverviewResponse> getRecommendations(Long productId, int limit) {
        if (!contentBasedMap.containsKey(productId))
            return Collections.emptyList();

        List<Long> productIds = contentBasedMap.get(productId).stream()
                .limit(limit)
                .map(ProductSimilarity::productId)
                .toList();

        return productClient.getProductsByIds(productIds);
    }

    List<ProductSimilarity> findSimilarProducts(ProductOverviewResponse targetProduct, List<ProductOverviewResponse> allProducts) {
        return allProducts.stream()
                .filter(product ->
                        !product.id().equals(targetProduct.id())
                                && !targetProduct.relatedProductIds().contains(product.id())
                )
                .map(product -> {
                    double similarity = similarityService.calculateSimilarity(targetProduct, product);
                    return ProductSimilarity.builder()
                            .productId(product.id())
                            .similarity(similarity)
                            .build();
                })
                .filter(product -> product.similarity() > MIN_SIMILARITY)
                .sorted(Comparator.comparing(ProductSimilarity::similarity).reversed())
                .collect(Collectors.toList());
    }

}
