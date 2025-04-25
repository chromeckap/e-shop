package com.ecommerce.attributevalue;

import com.ecommerce.exception.AttributeValueNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttributeValueValidator {
    private final AttributeValueRepository attributeValueRepository;

    public void validateAllAttributeValuesExist(Set<Long> attributeValueIds) {
        int count = attributeValueRepository.countByIds(attributeValueIds);

        if (attributeValueIds.isEmpty()) {
            log.debug("Skipping validation, no attribute value IDs provided.");
            return;
        }

        log.debug("Validating existence of attribute values: {}", attributeValueIds);

        if (attributeValueIds.size() != count)
            throw new AttributeValueNotFoundException("Jedna nebo více hodnot atributů neexistuje.");
    }
}
