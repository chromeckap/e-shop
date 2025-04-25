package com.ecommerce.product;

import com.ecommerce.productimage.ProductImageResponse;
import com.ecommerce.settings.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductOverviewResponse>> getAllProducts(
            @RequestParam(defaultValue = Constants.PAGE_NUMBER + "") int pageNumber,
            @RequestParam(defaultValue = Constants.PAGE_SIZE + "") int pageSize,
            @RequestParam(defaultValue = Constants.DIRECTION) String direction,
            @RequestParam(required = false, defaultValue = Constants.DEFAULT_SORT_ATTRIBUTE) String attribute
    ) {
        log.info("Fetching all products with pagination: page {}, size {}, sort by {}", pageNumber, pageSize, direction);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), attribute);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        Page<ProductOverviewResponse> response = productService.getAllProducts(pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductOverviewResponse>> getProductsByCategory(
            @RequestParam(defaultValue = Constants.PAGE_NUMBER + "") int pageNumber,
            @RequestParam(defaultValue = Constants.PAGE_SIZE + "") int pageSize,
            @RequestParam(defaultValue = Constants.DIRECTION) String direction,
            @RequestParam(required = false, defaultValue = Constants.DEFAULT_SORT_ATTRIBUTE) String attribute,
            @RequestParam(required = false) BigDecimal lowPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Set<Long> attributeValueIds,
            @PathVariable Long categoryId
    ) {
        log.info("Fetching products for category ID: {} with pagination: page {}, size {}, sort by {}", categoryId, pageNumber, pageSize, direction);
        Sort sort = Sort.by(Sort.Direction.fromString(direction), attribute);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        ProductSpecificationRequest specifications = new ProductSpecificationRequest(lowPrice, maxPrice, attributeValueIds);
        Page<ProductOverviewResponse> response = productService.getProductsByCategory(categoryId, specifications, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}/ranges")
    public ResponseEntity<FilterRangesResponse> getFilterRangesByCategory(@PathVariable Long categoryId) {
        FilterRangesResponse response = productService.getFilterRangesByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<ProductOverviewResponse>> getProductsByIds(@RequestParam List<Long> ids) {
        log.info("Batching products by IDs: {}", ids);
        List<ProductOverviewResponse> response = productService.getProductsByIds(ids);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductOverviewResponse>> searchProductsByQuery(
            @RequestParam(defaultValue = Constants.PAGE_NUMBER + "") int pageNumber,
            @RequestParam(defaultValue = Constants.PAGE_SIZE + "") int pageSize,
            @RequestParam(defaultValue = "") String query
    ) {
        log.info("Searching products with query: {}", query);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<ProductOverviewResponse> response = productService.searchProductsByQuery(query, pageRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> createProduct(@RequestBody @Valid ProductRequest request) {
        log.info("Creating new product: {}", request);
        Long response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequest request

    ) {
        log.info("Updating product with ID: {}", id);
        Long response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        log.info("Deleting product with ID: {}", id);
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadProductImages(
            @PathVariable Long id,
            @RequestPart("file") List<MultipartFile> files
    ) {
        log.info("Uploading pictures for product by ID: {}", id);
        productService.uploadProductImages(id, files);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}/images/{fileName}")
    public ResponseEntity<Resource> getImage(
            @PathVariable Long id,
            @PathVariable String fileName
    ) {
        ProductImageResponse response = productService.getImage(id, fileName);

        if (response == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(response.mediaType())
                .body(response.resource());
    }
}