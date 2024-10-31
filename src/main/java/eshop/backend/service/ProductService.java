package eshop.backend.service;

import eshop.backend.exception.CategoryNotFoundException;
import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.model.Product;
import eshop.backend.request.ProductRequest;
import eshop.backend.request.ProductSearchRequest;
import eshop.backend.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {
    Product createProduct(ProductRequest request) throws CategoryNotFoundException;
    ProductResponse getProduct(Long productId) throws ProductNotFoundException;
    Product updateProduct(ProductRequest request) throws ProductNotFoundException, CategoryNotFoundException;
    void deleteProduct(Long productId) throws ProductNotFoundException;
    Page<Product> pageByCategoryAndSpecifications(Long categoryId, ProductSearchRequest searchRequest, PageRequest pageRequest) throws CategoryNotFoundException;
    Page<Product> pageOfAllProducts(PageRequest request);
    List<Product> searchProductsByQuery(String query);
}
