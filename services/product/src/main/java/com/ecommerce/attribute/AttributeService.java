package com.ecommerce.attribute;

import com.ecommerce.attributevalue.AttributeValueService;
import com.ecommerce.exception.AttributeNotFoundException;
import com.ecommerce.product.Product;
import com.ecommerce.product.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttributeService {
    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;
    private final AttributeValidator attributeValidator;
    private final AttributeValueService attributeValueService;

    /**
     * Retrieves an attribute entity by its ID.
     *
     * @param id the ID of the attribute (must not be null)
     * @return the found Attribute entity
     * @throws AttributeNotFoundException if the attribute is not found
     */
    @Transactional(readOnly = true)
    public Attribute findAttributeEntityById(Long id) {
        Objects.requireNonNull(id, "ID atributu nesmí být prázdné.");
        log.debug("Fetching attribute with ID: {}", id);

        return attributeRepository.findById(id)
                .orElseThrow(() -> new AttributeNotFoundException(
                        String.format("Atribut s ID %s nebyl nalezen.", id)
                ));
    }

    /**
     * Processes and retrieves the attributes associated with a given product request.
     *
     * @param request the product request containing attribute IDs (must not be null)
     * @return a set of associated attributes
     */
    @Transactional
    public Set<Attribute> processProductAttributes(ProductRequest request) {
        Objects.requireNonNull(request, "Požadavek produktu nesmí být prázdný.");
        log.debug("Processing attributes for product: {}", request);

        Set<Long> attributeIds = request.attributeIds();

        if (attributeIds == null || attributeIds.isEmpty())
            return Collections.emptySet();

        attributeValidator.validateAllAttributesExist(attributeIds);

        List<Attribute> attributes = attributeRepository.findAllById(attributeIds);
        return Set.copyOf(attributes);
    }

    /**
     * Retrieves an attribute response by ID.
     *
     * @param id the attribute ID (must not be null)
     * @return the corresponding AttributeResponse DTO
     */
    @Transactional(readOnly = true)
    public AttributeResponse getAttributeById(Long id) {
        Objects.requireNonNull(id, "ID atributu nesmí být prázdné.");
        log.debug("Fetching attribute response for ID: {}", id);

        Attribute attribute = this.findAttributeEntityById(id);
            return attributeMapper.toResponse(attribute);
    }

    /**
     * Retrieves all attributes in an overview format.
     *
     * @return a set of AttributeResponse DTOs
     */
    @Transactional(readOnly = true)
    public Set<AttributeResponse> getAllAttributes() {
        log.debug("Fetching all attributes");
        return attributeRepository.findAll().stream()
                .map(attributeMapper::toResponse)
                .collect(Collectors.toSet());
    }

    /**
     * Creates a new attribute.
     *
     * @param request the attribute request DTO (must not be null)
     * @return the ID of the newly created attribute
     */
    @Transactional
    public Long createAttribute(AttributeRequest request) {
        Objects.requireNonNull(request, "Požadavek na atribut nesmí být prázdný.");
        log.debug("Creating new attribute with request: {}", request);

        Attribute attribute = attributeMapper.toAttribute(request);
        Attribute savedAttribute = attributeRepository.save(attribute);
        log.info("Attribute successfully created with ID: {}", savedAttribute.getId());

        attributeValueService.manageAttributeValues(savedAttribute, request.values());
        return savedAttribute.getId();
    }

    /**
     * Updates an existing attribute.
     *
     * @param id      the ID of the attribute to update (must not be null)
     * @param request the updated attribute request DTO (must not be null)
     * @return the ID of the updated attribute
     */
    @Transactional
    public Long updateAttribute(Long id, AttributeRequest request) {
        Objects.requireNonNull(id, "ID atributu nesmí být prázdné.");
        Objects.requireNonNull(request, "Požadavek na atribut nesmí být prázdný.");

        log.debug("Updating attribute with ID: {} using request: {}", id, request);
        Attribute existingAttribute = this.findAttributeEntityById(id);
        Attribute updatedAttribute = attributeMapper.toAttribute(request);

        updatedAttribute.setId(existingAttribute.getId());
        updatedAttribute.setValues(existingAttribute.getValues());

        Attribute savedAttribute = attributeRepository.save(updatedAttribute);
        log.info("Attribute successfully updated with ID: {}", savedAttribute.getId());

        attributeValueService.manageAttributeValues(savedAttribute, request.values());
        return savedAttribute.getId();
    }

    /**
     * Deletes an attribute by its ID.
     *
     * @param id the attribute ID (must not be null)
     */
    @Transactional
    public void deleteAttributeById(Long id) {
        Objects.requireNonNull(id, "ID atributu nesmí být prázdné.");
        log.debug("Deleting attribute with ID: {}", id);

        Attribute attribute = this.findAttributeEntityById(id);

        attributeRepository.delete(attribute);
        log.info("Attribute with ID {} successfully deleted.", id);
    }

    public Set<AttributeResponse> getAttributesByProducts(List<Product> products) {
        return products.stream()
                .flatMap(product -> product.getAttributes().stream())
                .map(attributeMapper::toResponse)
                .collect(Collectors.toSet());
    }
}
