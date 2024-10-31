package eshop.backend.service;

import eshop.backend.exception.CategoryNotFoundException;
import eshop.backend.exception.DiscountNotFoundException;
import eshop.backend.exception.InfiniteLoopException;
import eshop.backend.model.Category;
import eshop.backend.request.CategoryRequest;

import java.util.List;

public interface CategoryService {
    Category create(CategoryRequest request) throws CategoryNotFoundException, InfiniteLoopException, DiscountNotFoundException;
    Category read(Long categoryId) throws CategoryNotFoundException;
    Category update(CategoryRequest request) throws CategoryNotFoundException, InfiniteLoopException, DiscountNotFoundException;
    void delete(Long categoryId) throws CategoryNotFoundException;
    List<Category> list();

}
