package com.ecommerce.attribute;

import com.ecommerce.attributevalue.AttributeValueResponse;
import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record AttributeResponse(
        Long id,
        String name,
        List<AttributeValueResponse> values
) {}
