package com.ecommerce.attributevalue;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AttributeValueMapper {
    public AttributeValue toAttributeValue(@NonNull AttributeValueRequest request) {
        log.debug("Mapping AttributeValueRequest to Attribute Value: {}", request);
        return AttributeValue.builder()
                .value(request.value())
                .build();
    }

    public AttributeValueResponse toResponse(@NonNull AttributeValue attributeValue) {
        log.debug("Mapping Attribute Value to AttributeValueResponse: {}", attributeValue);
        return AttributeValueResponse.builder()
                .id(attributeValue.getId())
                .value(attributeValue.getValue())
                .attributeName(attributeValue.getAttribute().getName())
                .attributeId(attributeValue.getAttribute().getId())
                .build();
    }
}
