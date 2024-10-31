package eshop.backend.service.impl;

import eshop.backend.exception.CategoryNotFoundException;
import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.model.*;
import eshop.backend.repository.CategoryRepository;
import eshop.backend.repository.ProductRepository;
import eshop.backend.request.ProductRequest;
import eshop.backend.repository.specification.ProductSearchSpecification;
import eshop.backend.request.ProductSearchRequest;
import eshop.backend.response.ProductOverviewResponse;
import eshop.backend.response.ProductPriceOverview;
import eshop.backend.response.ProductResponse;
import eshop.backend.service.ProductService;
import eshop.backend.service.ReviewService;
import eshop.backend.service.VariantService;
import eshop.backend.service.utils.ProductSidebarFilter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static eshop.backend.utils.EntityUtils.findByIdOrElseThrow;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSidebarFilter productsAttributeFilter; //todo product
    private final VariantService variantService;
    private final ReviewService reviewService;

    @Override
    public Product createProduct(ProductRequest request) throws CategoryNotFoundException {
        var product = new Product(request);

        setCategoryIfSelectedAndExists(product, request);
        manageRelatedProductsIfExist(product, request);

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(ProductRequest request) throws ProductNotFoundException, CategoryNotFoundException {
        var product = findByIdOrElseThrow(request.id(), productRepository, ProductNotFoundException::new);

        updateProductProperties(product, request);
        setCategoryIfSelectedAndExists(product, request);
        manageRelatedProductsIfExist(product, request);

        return productRepository.save(product);
    }

    @Override
    public ProductResponse getProduct(Long productId) throws ProductNotFoundException {
        var product = findByIdOrElseThrow(productId, productRepository, ProductNotFoundException::new);

        return buildProductResponse(product);
    }

    @Override
    public void deleteProduct(Long productId) throws ProductNotFoundException {
        var product = findByIdOrElseThrow(productId, productRepository, ProductNotFoundException::new);
        productRepository.delete(product);
    }

    @Override
    public Page<Product> pageOfAllProducts(PageRequest request) {
        return productRepository.findAll(request);
    }

    //todo cache
    //todo pages = <ProductOverviewResponse>
    // only use the specifications, not both!
    @Override
    public Page<Product> pageByCategoryAndSpecifications(Long categoryId, ProductSearchRequest searchRequest, PageRequest pageRequest) throws CategoryNotFoundException {
        var category = findByIdOrElseThrow(categoryId, categoryRepository, CategoryNotFoundException::new);
        var specification = new ProductSearchSpecification(searchRequest);

        /*
        Map<Attribute, Map<AttributeValue, Long>> attributeValuesMap = productsAttributeFilter.listAttributesAndValuesCountByCategory(category, productPage.getContent());
        BigDecimal minPrice = productsAttributeFilter.minPrice(attributeValuesMap);
        BigDecimal maxPrice = productsAttributeFilter.maxPrice(attributeValuesMap);

         */
        return productRepository.findAllByCategory(category, specification, pageRequest);
    }

    //todo list

    @Override
    public List<Product> searchProductsByQuery(String query) {
        if (query.isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    private void setCategoryIfSelectedAndExists(Product product, ProductRequest request) throws CategoryNotFoundException {
        if (request.categoryId() == null)
            return;

        var category = findByIdOrElseThrow(request.categoryId(), categoryRepository, CategoryNotFoundException::new);

        product.setCategory(category);
    }

    private void manageRelatedProductsIfExist(Product product, ProductRequest request) {
        if (request.relatedProductIds() == null || request.relatedProductIds().isEmpty())
            return;

        var relatedProductIds = request.relatedProductIds();

        if (relatedProductIds.contains(product.getId())) {
            throw new IllegalArgumentException("The product cannot be relationally linked to itself.");
        }
        var products = productRepository.findAllById(relatedProductIds);

        product.setRelatedProducts(new HashSet<>(products));
    }

    private void updateProductProperties(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
    }

    private ProductResponse buildProductResponse(Product product) {
        var response = new ProductResponse(product);
        //response.setVariants(variantService.listByProduct(product));
        response.setRatingSummary(reviewService.getRatingSummary(product));
        //response.setSingleVariant(response.getVariants().size() > 1);
        //response.setPriceOverview(manageProductPriceOverview(product));
        if (product.getRelatedProducts() != null) {
            response.setRelatedProducts(product.getRelatedProducts().stream()
                    .filter(Product::isVisible)
                    .map(this::buildProductOverviewResponse)
                    .collect(Collectors.toSet()));
        } else {
            response.setRelatedProducts(Collections.emptySet()); // or null, depending on your requirements
        }
        return response;
    }

    private ProductOverviewResponse buildProductOverviewResponse(Product product) {
        var response = new ProductOverviewResponse(product);

        //response.setPriceOverview(manageProductPriceOverview(product));
        response.setRatingSummary(reviewService.getRatingSummary(product));

        return response;
    }

    private ProductPriceOverview manageProductPriceOverview(Product product) {
        var isVariantsPricesEqual = isVariantsPricesEqual(product);
        var cheapestVariant = getCheapestVariantPrice(product);

        return new ProductPriceOverview(
                cheapestVariant.getBasePrice(),
                cheapestVariant.getDiscountedPrice(),
                isVariantsPricesEqual
        );
    }

    private Variant getCheapestVariantPrice(Product product) {
        return product.getVariants().stream()
                .min(Comparator.comparing(Variant::getBasePrice))
                .orElseThrow(() -> new NoSuchElementException("No variants available."));
    }

    private boolean isVariantsPricesEqual(Product product) {
        return product.getVariants().stream()
                .map(Variant::getBasePrice)
                .distinct()
                .count() == 1;
    }
}
