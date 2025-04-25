package com.ecommerce.variant;

import com.ecommerce.attributevalue.AttributeValueService;
import com.ecommerce.exception.VariantNotFoundException;
import com.ecommerce.product.Product;
import com.ecommerce.product.ProductService;
import com.ecommerce.variant.purchase.CartItemRequest;
import com.ecommerce.variant.purchase.PurchaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VariantService {
    private final VariantRepository variantRepository;
    private final VariantMapper variantMapper;
    private final VariantValidator variantValidator;
    private final AttributeValueService attributeValueService;
    private final ProductService productService;

    /**
     * Finds a variant entity by ID.
     *
     * @param id the variant ID
     * @return the variant entity
     * @throws VariantNotFoundException if not found
     */
    @Transactional(readOnly = true)
    public Variant findVariantEntityById(Long id) {
        Objects.requireNonNull(id, "ID varianty nesmí být prázdné.");
        log.debug("Fetching variant by ID: {}", id);

        return variantRepository.findById(id)
                .orElseThrow(() -> new VariantNotFoundException(
                        String.format("Varianta s ID %s nebyl nalezen.", id)
                ));
    }

    /**
     * Retrieves a variant by ID.
     *
     * @param id the variant ID
     * @return the variant response DTO
     */
    @Transactional(readOnly = true)
    public VariantResponse getVariantById(Long id) {
        Objects.requireNonNull(id, "ID varianty nesmí být prázdné.");
        log.debug("Fetching variants for product ID: {}", id);

        Variant variant = this.findVariantEntityById(id);
        return variantMapper.toResponse(variant);
    }

    /**
     * Retrieves all variants by product ID.
     *
     * @param id the product ID
     * @return set of variant responses
     */
    @Transactional(readOnly = true)
    public Set<VariantResponse> getVariantsByProductId(Long id) {
        Objects.requireNonNull(id, "ID produktu nesmí být prázdné.");
        log.debug("Fetching variants for product ID: {}", id);

        Product product = productService.findProductEntityById(id);
        List<Variant> variants = variantRepository.findAllByProduct(product);

        return variants.stream()
                .map(variantMapper::toResponse)
                .collect(Collectors.toSet());
    }

    /**
     * Creates a new variant.
     *
     * @param request the variant request DTO
     * @return the created variant ID
     */
    @Transactional
    public Long createVariant(VariantRequest request) {
        Objects.requireNonNull(request, "Požadavek varianty nesmí být prázdný.");
        log.debug("Creating new variant with request: {}", request);

        Variant variant = variantMapper.toVariant(request);
        this.assignVariantDetails(variant, request);
        variantValidator.validateProductNumberVariants(variant);

        Variant savedVariant = variantRepository.save(variant);
        log.info("Variant created successfully with ID: {}", savedVariant.getId());

        return savedVariant.getId();
    }

    /**
     * Updates an existing variant.
     *
     * @param id      the variant ID
     * @param request the updated variant request
     * @return the updated variant ID
     */
    @Transactional
    public Long updateVariant(
            Long id,
            VariantRequest request
    ) {
        Objects.requireNonNull(id, "ID varianty nesmí být prázdné.");
        Objects.requireNonNull(request, "Požadavek varianty nesmí být prázdný.");
        log.debug("Updating variant ID: {}", id);

        Variant existingVariant = this.findVariantEntityById(id);
        Variant updatedVariant = variantMapper.toVariant(request);

        updatedVariant.setId(existingVariant.getId());
        this.assignVariantDetails(updatedVariant, request);

        Variant savedVariant = variantRepository.save(updatedVariant);
        log.info("Variant updated successfully with ID: ID {}", savedVariant.getId());

        return savedVariant.getId();
    }

    /**
     * Deletes a variant by ID.
     *
     * @param id the variant ID
     */
    @Transactional
    public void deleteVariantById(Long id) {
        Objects.requireNonNull(id, "ID varianty nesmí být prázdné.");
        log.debug("Deleting variant with ID: {}", id);

        Variant variant = this.findVariantEntityById(id);

        variantRepository.delete(variant);
        log.info("Variant deleted successfully with ID: {}", id);
    }

    /**
     * Assigns product and attribute values to the variant.
     *
     * @param variant the variant entity
     * @param request the variant request DTO
     */
    private void assignVariantDetails(Variant variant, VariantRequest request) {
        log.debug("Assigning details to variant: {}", variant);

        variant.setProduct(productService.findProductEntityById(request.productId()));
        variant.setValues(attributeValueService.processVariantAttributeValues(request));
    }

    public List<PurchaseResponse> getVariantsByCartItems(List<CartItemRequest> cartItems) {
        List<Long> variantIds = cartItems.stream()
                .map(CartItemRequest::variantId)
                .collect(Collectors.toList());

        List<Variant> variants = variantRepository.findAllById(variantIds);

        Map<Long, Variant> variantMap = variants.stream()
                .collect(Collectors.toMap(Variant::getId, variant -> variant));

        return cartItems.stream()
                .map(item -> {
                    Variant variant = variantMap.get(item.variantId());
                    if (variant == null) {
                        return null;
                    }
                    return variantMapper.toPurchaseResponse(variant, item.quantity());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
