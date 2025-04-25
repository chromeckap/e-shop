package com.ecommerce.category;

import com.ecommerce.exception.CategoryNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidator categoryValidator;

    /**
     * Finds a category entity by ID that can be used elsewhere.
     *
     * @param id the ID of the category
     * @return the Category entity
     * @throws CategoryNotFoundException if the category does not exist
     */
    @Transactional(readOnly = true)
    public Category findCategoryEntityById(Long id) {
        Objects.requireNonNull(id, "ID kategorie nesmí být prázdné.");
        log.debug("Searching for category with ID: {}", id);

        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(
                        String.format("Kategorie s ID %s nebyla nalezena.", id)
                ));
    }

    /**
     * Gets a category by ID, returns a response DTO.
     *
     * @param id the ID of the category
     * @return the CategoryResponse DTO
     * @throws CategoryNotFoundException if the category does not exist
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(@NonNull Long id) {
        Objects.requireNonNull(id, "ID kategorie nesmí být prázdné.");
        log.debug("Fetching category response for category with ID: {}", id);

        Category category = this.findCategoryEntityById(id);
        return categoryMapper.toResponse(category);
    }

    /**
     * Gets multiple categories by a set of IDs.
     *
     * @param ids the set of category IDs
     * @return the list of CategoryOverviewResponse DTOs
     * @throws CategoryNotFoundException if the category does not exist
     */
    @Transactional(readOnly = true)
    public List<CategoryOverviewResponse> getCategoriesByIds(@NonNull Set<Long> ids) {
        Objects.requireNonNull(ids, "ID kategorií nesmí být prázdné.");
        log.debug("Validating existence of categories for IDs: {}", ids);

        categoryValidator.validateCategoriesExist(ids);

        log.debug("Fetching categories for valid IDs.");
        return categoryRepository.findAllById(ids).stream()
                .map(categoryMapper::toOverviewResponse)
                .collect(Collectors.toList());
    }

    /**
     * Gets all categories by loading root categories with their children.
     *
     * @return the list of all root CategoryOverviewResponse DTOs as a tree
     */
    @Transactional(readOnly = true)
    public List<CategoryOverviewResponse> getAllCategories() {
        log.debug("Fetching all root categories.");
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        this.loadChildren(rootCategories);

        return rootCategories.stream()
                .map(categoryMapper::toOverviewResponse)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new category.
     *
     * @param request the CategoryRequest DTO
     * @return the ID of the newly created category
     */
    @Transactional
    public Long createCategory(CategoryRequest request) {
        Objects.requireNonNull(request, "Požadavek na kategorii nesmí být prázdný.");
        log.debug("Creating category with request: {}", request);

        Category category = categoryMapper.toCategory(request);
        Category parentCategory = Optional.ofNullable(request.parentId())
                .map(this::findCategoryEntityById)
                .orElse(null);

        categoryValidator.validateNoInfiniteLoop(category, parentCategory);
        category.setParent(parentCategory);

        Category savedCategory = categoryRepository.save(category);
        log.info("Category successfully created with ID: {}", savedCategory.getId());

        return savedCategory.getId();
    }

    /**
     * Updates an existing category.
     *
     * @param id the ID of the category to update
     * @param request the CategoryRequest DTO containing the updated data
     * @return the ID of the updated category
     */
    @Transactional
    public Long updateCategory(Long id, CategoryRequest request) {
        Objects.requireNonNull(id, "ID kategorie nesmí být prázdné.");
        Objects.requireNonNull(request, "Požadavek na kategorii nesmí být prázdný.");

        log.debug("Updating category with ID: {} using request: {}", id, request);

        Category existingCategory = this.findCategoryEntityById(id);
        Category updatedCategory = categoryMapper.toCategory(request);

        updatedCategory.setId(existingCategory.getId());
        updatedCategory.setChildren(existingCategory.getChildren());

        Category parentCategory = Optional.ofNullable(request.parentId())
                .map(this::findCategoryEntityById)
                .orElse(null);

        categoryValidator.validateNoInfiniteLoop(updatedCategory, parentCategory);
        updatedCategory.setParent(parentCategory);

        Category savedCategory = categoryRepository.save(updatedCategory);
        log.info("Category successfully updated with ID: {}", savedCategory.getId());

        return savedCategory.getId();
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete
     * @throws CategoryNotFoundException if the category does not exist
     */
    @Transactional
    public void deleteCategoryById(Long id) {
        Objects.requireNonNull(id, "ID kategorie nesmí být prázdné.");
        log.debug("Deleting category with ID: {}", id);

        Category category = this.findCategoryEntityById(id);

        categoryRepository.delete(category);
        log.info("Category successfully deleted with ID: {}", id);
    }

    /**
     * Recursively loads the children of the given categories.
     *
     * @param categories the list of categories to load children for
     */
    private void loadChildren(List<Category> categories) {
        Objects.requireNonNull(categories, "Kategorie nesmí být prázdné.");

        categories.forEach(category -> {
            List<Category> children = category.getChildren();
            if (!children.isEmpty()) {
                log.debug("Loading children for category with ID: {}", category.getId());
                this.loadChildren(children);
            }
        });
    }
}
