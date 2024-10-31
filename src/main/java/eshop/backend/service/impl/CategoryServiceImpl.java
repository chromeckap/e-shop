package eshop.backend.service.impl;

import eshop.backend.exception.CategoryNotFoundException;
import eshop.backend.exception.DiscountNotFoundException;
import eshop.backend.exception.InfiniteLoopException;
import eshop.backend.model.Category;
import eshop.backend.repository.CategoryRepository;
import eshop.backend.repository.DiscountRepository;
import eshop.backend.request.CategoryRequest;
import eshop.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static eshop.backend.utils.EntityUtils.findByIdOrElseThrow;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final DiscountRepository discountRepository;

    @Override
    public Category create(CategoryRequest request) throws CategoryNotFoundException, InfiniteLoopException, DiscountNotFoundException {
        var category = new Category(request);

        manageDiscountIfExists(category, request);
        setParentCategoryIfExists(category, request);

        return categoryRepository.save(category);
    }

    @Override
    public Category read(Long categoryId) throws CategoryNotFoundException {
        return findByIdOrElseThrow(categoryId, categoryRepository, CategoryNotFoundException::new);
    }

    @Override
    public Category update(CategoryRequest request) throws CategoryNotFoundException, InfiniteLoopException, DiscountNotFoundException {
        var category = read(request.id());

        category.setName(request.name());
        manageDiscountIfExists(category, request);
        setParentCategoryIfExists(category, request);

        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long categoryId) throws CategoryNotFoundException {
        var category = read(categoryId);

        removeParentFromChildCategories(category);

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<Category> list() {
        return categoryRepository.findAll();
    }

    private void setParentCategoryIfExists(Category category, CategoryRequest request) throws CategoryNotFoundException, InfiniteLoopException {
        if (request.parentId() != null) {
            var parent = findByIdOrElseThrow(request.parentId(), categoryRepository, CategoryNotFoundException::new);

            category.setParent(parent);

            if (isInfiniteLoop(category, parent))
                throw new InfiniteLoopException("Infinite loop, wrong parentId.");
        }
    }

    private void removeParentFromChildCategories(Category category) {
        category.getChildCategories()
                .forEach(childCategory -> childCategory.setParent(null));
    }

    private boolean isInfiniteLoop(Category category, Category parent) {
        while (parent != null) {
            if (parent.getId().equals(category.getId())) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    private void manageDiscountIfExists(Category category, CategoryRequest request) throws DiscountNotFoundException {
        if (request.discoundId() != null) {
            var discount = findByIdOrElseThrow(request.discoundId(), discountRepository, DiscountNotFoundException::new);
            category.setDiscount(discount);
        }
    }
}