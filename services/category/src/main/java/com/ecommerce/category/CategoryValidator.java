package com.ecommerce.category;

import com.ecommerce.exception.CategoryNotFoundException;
import com.ecommerce.exception.InfiniteLoopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CategoryValidator {
    private final CategoryRepository categoryRepository;

    public void validateNoInfiniteLoop(Category category, Category parentCategory) {
        if (parentCategory == null)
            return;

        log.debug("Validating infinite loop: category={}, parentCategory={}", category.getId(), parentCategory.getId());
        if (parentCategory.getId().equals(category.getId()))
            throw new InfiniteLoopException(
                    String.format("Společně s kategorií '%s' vzniká nekonečná smyčka.", category.getName())
            );
        this.validateNoInfiniteLoop(category, parentCategory.getParent());
    }

    public void validateCategoriesExist(Set<Long> categoryIds) {
        if (categoryIds.isEmpty()) {
            log.debug("Skipping validation, no category IDs provided.");
            return;
        }

        int count = categoryRepository.countCategoriesByIds(categoryIds);
        log.debug("Validating existence of categories: {}", categoryIds);
        if (categoryIds.size() != count)
            throw new CategoryNotFoundException("Jedna z kategorií neexistuje.");
    }
}
