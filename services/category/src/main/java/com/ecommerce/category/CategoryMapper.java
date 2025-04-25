package com.ecommerce.category;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryMapper {
    public Category toCategory(@NonNull CategoryRequest request) {
        log.debug("Mapping CategoryRequest to Category: {}", request);
        return Category.builder()
                .id(request.id())
                .name(request.name())
                .description(request.description())
                .build();
    }

    public CategoryResponse toResponse(@NonNull Category category) {
        log.debug("Mapping Category to CategoryResponse: {}", category);
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parent(this.mapParentFor(category))
                .children(this.mapChildrenFor(category))
                .build();
    }

    public CategoryOverviewResponse toOverviewResponse(@NonNull Category category) {
        log.debug("Mapping Category to CategoryOverviewResponse: {}", category);
        return CategoryOverviewResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .children(this.mapChildrenFor(category))
                .build();
    }

    private CategoryOverviewResponse mapParentFor(Category category) {
        return Optional.ofNullable(category.getParent())
                .map(this::toOverviewResponse)
                .orElse(null);
    }

    private List<CategoryOverviewResponse> mapChildrenFor(Category category) {
        return Optional.ofNullable(category.getChildren())
                .map(children -> children.stream()
                        .map(this::toOverviewResponse)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}