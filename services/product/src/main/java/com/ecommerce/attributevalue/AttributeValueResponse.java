package com.ecommerce.attributevalue;

import lombok.Builder;

@Builder
public record AttributeValueResponse (
        Long id,
        String value,
        String attributeName,
        Long attributeId
) {}
