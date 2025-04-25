package com.ecommerce.attribute;

import com.ecommerce.exception.AttributeNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
@Slf4j
public class AttributeValidator {
    private final AttributeRepository attributeRepository;

    public void validateAllAttributesExist(Set<Long> attributeIds) {
        if (attributeIds.isEmpty()) {
            log.debug("Skipping validation, no attribute IDs provided.");
            return;
        }

        int count = attributeRepository.countByIds(attributeIds);

        log.debug("Validating existence of attributes: {}", attributeIds);
        if (attributeIds.size() != count)
            throw new AttributeNotFoundException("Jeden nebo více atributů neexistuje.");
    }

}
