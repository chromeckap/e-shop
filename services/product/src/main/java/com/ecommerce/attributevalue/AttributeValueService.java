package com.ecommerce.attributevalue;

import com.ecommerce.attribute.Attribute;
import com.ecommerce.variant.VariantRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttributeValueService {
    private final AttributeValueRepository attributeValueRepository;
    private final AttributeValueMapper attributeValueMapper;
    private final AttributeValueValidator attributeValueValidator;

    /**
     * Processes attribute values for a product variant.
     *
     * @param request the variant request
     * @return a list of attribute values
     */
    @Transactional
    public List<AttributeValue> processVariantAttributeValues(VariantRequest request) {
        Objects.requireNonNull(request, "Požadavek varianty nesmí být prázdný.");
        log.debug("Processing attribute values for variant: {}", request);

        Set<Long> attributeValueIds = request.attributeValueIds();

        if (attributeValueIds == null || attributeValueIds.isEmpty())
            return Collections.emptyList();

        attributeValueValidator.validateAllAttributeValuesExist(attributeValueIds);

        List<AttributeValue> attributeValues = attributeValueRepository.findAllById(attributeValueIds);
        return List.copyOf(attributeValues);
    }

    /**
     * Manages attribute values by updating existing ones and adding new ones.
     *
     * @param attribute the attribute to which the values belong
     * @param requests  the list of attribute value requests
     */
    @Transactional
    public void manageAttributeValues(Attribute attribute, List<AttributeValueRequest> requests) {
        Objects.requireNonNull(attribute, "Atribut nesmí být prázdný.");
        log.debug("Managing attribute values for attribute: {}", attribute);

        if (requests == null || requests.isEmpty())
            return;

        List<AttributeValue> existingValues = attributeValueRepository.findByAttribute(attribute);

        List<AttributeValue> toSave = prepareAttributeValues(attribute, requests, existingValues);
        List<AttributeValue> toDelete = findValuesToDelete(requests, existingValues);

        if (!toSave.isEmpty()) {
            attributeValueRepository.saveAll(toSave);
            log.info("Saved {} new or updated attribute values.", toSave.size());
        }

        if (!toDelete.isEmpty()) {
            attributeValueRepository.deleteAll(toDelete);
            log.info("Deleted {} outdated attribute values.", toDelete.size());
        }
    }

    /**
     * Prepares attribute values for saving or updating.
     *
     * @param attribute      the attribute
     * @param requests       the requested attribute values
     * @param existingValues the current attribute values stored in the database
     * @return a list of attribute values to be saved
     */
    private List<AttributeValue> prepareAttributeValues(
            Attribute attribute,
            List<AttributeValueRequest> requests,
            List<AttributeValue> existingValues
    ) {
        Map<Long, AttributeValue> existingValueMap = existingValues.stream()
                .collect(Collectors.toMap(AttributeValue::getId, Function.identity()));

        List<AttributeValue> toSave = new ArrayList<>();

        for (AttributeValueRequest request : requests) {
            if (attributeValueRepository.existsAttributeValueByIdAndAttribute(request.id(), attribute)) {
                AttributeValue existingValue = existingValueMap.get(request.id());
                existingValue.setValue(request.value());
                toSave.add(existingValue);
            } else {
                AttributeValue newValue = new AttributeValue();
                newValue.setAttribute(attribute);
                newValue.setValue(request.value());
                toSave.add(newValue);
            }
        }

        return toSave;
    }

    /**
     * Finds attribute values that are no longer in the request and should be deleted.
     *
     * @param requests       the requested attribute values
     * @param existingValues the current attribute values stored in the database
     * @return a list of attribute values to be deleted
     */
    private List<AttributeValue> findValuesToDelete(
            List<AttributeValueRequest> requests,
            List<AttributeValue> existingValues
    ) {
        Set<Long> requestIds = requests.stream()
                .map(AttributeValueRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return existingValues.stream()
                .filter(existingValue -> !requestIds.contains(existingValue.getId()))
                .toList();
    }

}
