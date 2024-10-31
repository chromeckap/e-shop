package eshop.backend.controller;

import eshop.backend.exception.CategoryNotFoundException;
import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.model.Product;
import eshop.backend.request.ProductRequest;
import eshop.backend.request.ProductSearchRequest;
import eshop.backend.response.ProductResponse;
import eshop.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductRequest> createProduct(@Valid @RequestBody ProductRequest productRequest) throws CategoryNotFoundException {
        productService.createProduct(productRequest);
        return new ResponseEntity<>(productRequest, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> readProduct(@PathVariable Long id) throws ProductNotFoundException {
        var product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductRequest> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) throws ProductNotFoundException, CategoryNotFoundException {
        if (!Objects.equals(id, productRequest.id())) {
            throw new ProductNotFoundException(id);
        }
        productService.updateProduct(productRequest);

        return ResponseEntity.ok(productRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) throws ProductNotFoundException {
        productService.deleteProduct(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        List<Product> productRequestList = productService.searchProductsByQuery(query);

        return ResponseEntity.ok(productRequestList);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> getProductsInCategory(
            @RequestBody(required = false) ProductSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "27") Integer pageSize,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String attribute,
            @PathVariable Long categoryId) throws CategoryNotFoundException {
        var pageRequest = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(direction), attribute);
        var productPage = productService.pageByCategoryAndSpecifications(categoryId, searchRequest, pageRequest);

        return ResponseEntity.ok(productPage);
    }

    @GetMapping
    public ResponseEntity<Page<Product>> pageOfAllProducts(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String attribute) {
        var request = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(direction), attribute);

        return ResponseEntity.ok(productService.pageOfAllProducts(request));
    }
}