package com.ecommerce.product;

import com.ecommerce.attribute.AttributeService;
import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.feignclient.category.CategoryClient;
import com.ecommerce.productimage.ProductImageResponse;
import com.ecommerce.productimage.ProductImageService;
import com.ecommerce.relatedproduct.RelatedProductService;
import com.ecommerce.settings.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;
    private final RelatedProductService relatedProductService;
    private final ProductPriceService priceService;
    private final AttributeService attributeService;
    private final CategoryClient categoryClient;

    /**
     * Finds a product entity by ID.
     *
     * @param id the product ID
     * @return the product entity
     * @throws ProductNotFoundException if the product is not found
     */
    @Transactional(readOnly = true)
    public Product findProductEntityById(Long id) {
        Objects.requireNonNull(id, "ID produktu nesmí být prázdné.");
        log.debug("Fetching product by ID: {}", id);

        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Produkt s ID %s nebyl nalezen.", id)
                ));
    }

    /**
     * Retrieves a product by ID.
     *
     * @param id the product ID
     * @return the product response DTO
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Objects.requireNonNull(id, "ID produktu nesmí být prázdné.");
        log.debug("Fetching product response for ID: {}", id);

        Product product = this.findProductEntityById(id);
        return productMapper.toResponse(product);
    }

    /**
     * Retrieves all products with pagination.
     *
     * @param pageRequest pagination request
     * @return paginated product responses
     */
    @Transactional(readOnly = true)
    public Page<ProductOverviewResponse> getAllProducts(PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "Požadavek na stránkování nesmí být prázdný.");
        log.debug("Fetching all products with page request: {}", pageRequest);

        return productRepository.findAll(pageRequest)
                .map(productMapper::toOverviewResponse);
    }

    /**
     * Retrieves products by category with optional specifications.
     *
     * @param categoryId           category ID
     * @param specifications product specification filter
     * @param pageRequest          pagination request
     * @return paginated product responses
     */
    @Transactional(readOnly = true)
    public Page<ProductOverviewResponse> getProductsByCategory(
            Long categoryId,
            ProductSpecificationRequest specifications,
            PageRequest pageRequest
    ) {
        Objects.requireNonNull(categoryId, "ID kategorie nesmí být prázdné.");
        Objects.requireNonNull(pageRequest, "Požadavek na stránkování nesmí být prázdný.");
        log.debug("Fetching products for category ID: {}", categoryId);

        Specification<Product> categorySpec = (root, query, cb) ->
                cb.and(
                        cb.isMember(categoryId, root.get("categoryIds")),
                        cb.isTrue(root.get("isVisible"))
                );
        Specification<Product> finalSpec = categorySpec.and(new ProductSpecification(specifications));

        List<Product> products = productRepository.findAll(finalSpec);
        List<Product> sortedProducts = this.sortProducts(products, pageRequest.getSort());

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), sortedProducts.size());

        List<ProductOverviewResponse> pagedProducts = sortedProducts.subList(start, end)
                .stream()
                .map(productMapper::toOverviewResponse)
                .toList();

        return new PageImpl<>(pagedProducts, pageRequest, sortedProducts.size());
    }

    private List<Product> sortProducts(List<Product> products, Sort sort) {
        if (sort.isUnsorted()) {
            return products;
        }

        Sort.Order order = sort.iterator().next();
        Comparator<Product> comparator;

        switch (order.getProperty()) {
            case "id" -> comparator = Comparator.comparing(Product::getId);
            case "name" -> comparator = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
            case "price" -> comparator = Comparator.comparing(
                    priceService::getCheapestVariantPrice,
                    Comparator.nullsLast(BigDecimal::compareTo)
            );
            default -> {
                log.warn("Neplatný atribut pro řazení: {}", order.getProperty());
                return products;
            }
        }

        if (order.isDescending()) {
            comparator = comparator.reversed();
        }

        return products.stream()
                .sorted(comparator)
                .toList();
    }

    /**
     * Retrieves product overview responses for the given product IDs, preserving the original order.
     * Only visible products are included in the results.
     *
     * @param ids List of product IDs to retrieve
     * @return List of product overview responses in the same order as the input IDs (visible products only)
     */
    public List<ProductOverviewResponse> getProductsByIds(List<Long> ids) {
        Objects.requireNonNull(ids, "ID produktů nesmí být null.");
        log.debug("Fetching {} products by IDs: {}", ids.size(), ids);

        Map<Long, Integer> idPositionMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            idPositionMap.put(ids.get(i), i);
        }

        List<Product> visibleProducts = productRepository.findAllById(ids).stream()
                .filter(Product::isVisible)
                .collect(Collectors.toList());

        if (visibleProducts.isEmpty()) {
            return Collections.emptyList();
        }

        visibleProducts.sort(Comparator.comparingInt(product ->
                idPositionMap.getOrDefault(product.getId(), Integer.MAX_VALUE)));

        return visibleProducts.stream()
                .map(productMapper::toOverviewResponse)
                .collect(Collectors.toList());
    }


    /**
     * Searches for products based on a query.
     *
     * @param query       search query
     * @param pageRequest pagination request
     * @return paginated product responses
     */
    @Transactional(readOnly = true)
    public Page<ProductOverviewResponse> searchProductsByQuery(
            String query,
            PageRequest pageRequest
    ) {
        Objects.requireNonNull(query, "Query nesmí být prázdné.");
        Objects.requireNonNull(pageRequest, "Požadavek na stránkování nesmí být prázdný.");
        log.debug("Searching products with query: {}", query);

        return productRepository.findAllVisibleBySimilarity(
                query,
                Constants.MIN_SIMILARITY,
                pageRequest
        ).map(productMapper::toOverviewResponse);
    }

    /**
     * Creates a new product.
     *
     * @param request the product request DTO
     * @return the created product ID
     */
    @Transactional
    public Long createProduct(ProductRequest request) {
        Objects.requireNonNull(request, "Požadavek na produkt nesmí být prázdný.");
        log.debug("Creating product with request: {}", request);

        Product product = productMapper.toProduct(request);
        this.assignProductDetails(product, request);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully: ID {}, Name {}", savedProduct.getId(), savedProduct.getName());

        return savedProduct.getId();
    }

    /**
     * Updates an existing product.
     *
     * @param id      the product ID
     * @param request the updated product request
     * @return the updated product ID
     */
    @Transactional
    public Long updateProduct(
            Long id,
            ProductRequest request
    ) {
        Objects.requireNonNull(id, "ID produktu nesmí být prázdné.");
        Objects.requireNonNull(request, "Požadavek na produkt nesmí být prázdný.");
        log.debug("Updating product with ID: {} using request: {}", id, request);

        Product existingProduct = this.findProductEntityById(id);
        Product updatedProduct = productMapper.toProduct(request);

        updatedProduct.setId(existingProduct.getId());
        updatedProduct.setVariants(existingProduct.getVariants());
        updatedProduct.setImages(existingProduct.getImages());
        this.assignProductDetails(updatedProduct, request);

        Product savedProduct = productRepository.save(updatedProduct);
        log.info("Product updated successfully: ID {}, Name {}", savedProduct.getId(), savedProduct.getName());

        return savedProduct.getId();
    }

    /**
     * Deletes a product by ID.
     *
     * @param id the product ID
     */
    @Transactional
    public void deleteProductById(Long id) {
        Objects.requireNonNull(id, "ID produktu nesmí být prázdné.");
        log.debug("Deleting product with ID: {}", id);

        Product product = this.findProductEntityById(id);

        productRepository.delete(product);
        productImageService.deleteDirectory(id);
        log.info("Product deleted successfully: ID {}, Name {}", id, product.getName());
    }

    /**
     * Uploads images for a specific product.
     *
     * @param productId the ID of the product to upload images for
     * @param files list of image files to be processed
     * @throws IllegalArgumentException if product with given ID doesn't exist
     */
    @Transactional
    public void uploadProductImages(Long productId, List<MultipartFile> files) {
        Product product = this.findProductEntityById(productId);
        productImageService.processFilesForProduct(product, files);
    }

    /**
     * Retrieves a specific image for a product.
     *
     * @param id the product ID
     * @param fileName the name of the image file to retrieve
     * @return ProductImageResponse containing the image data
     * @throws IllegalArgumentException if product with given ID doesn't exist
     */
    @Transactional(readOnly = true)
    public ProductImageResponse getImage(Long id, String fileName) {
        Product product = this.findProductEntityById(id);
        return productImageService.getImage(product, fileName);
    }

    /**
     * Assigns category, related products, and attributes to the product.
     *
     * @param product the product entity
     * @param request the product request DTO
     */
    private void assignProductDetails(Product product, ProductRequest request) {
        log.debug("Assigning details to product: {}", product.getName());

        categoryClient.getCategoriesByIds(request.categoryIds());

        product.setCategoryIds(request.categoryIds());
        product.setRelatedProducts(relatedProductService.processRelatedProducts(product, request));
        product.setAttributes(attributeService.processProductAttributes(request));
    }

    public FilterRangesResponse getFilterRangesByCategory(Long categoryId) {
        List<Product> products = productRepository.findAllVisibleByCategory(categoryId);

        return FilterRangesResponse.builder()
                .lowPrice(priceService.getCheapestVariantPrice(products))
                .maxPrice(priceService.getHighestVariantPrice(products))
                .attributes(attributeService.getAttributesByProducts(products))
                .build();
    }
}