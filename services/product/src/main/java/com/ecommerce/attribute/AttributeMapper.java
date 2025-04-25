package com.ecommerce.attribute;

import com.ecommerce.attributevalue.AttributeValueMapper;
import com.ecommerce.attributevalue.AttributeValueResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttributeMapper {
    private final AttributeValueMapper attributeValueMapper;

    public Attribute toAttribute(@NonNull AttributeRequest request) {
        log.debug("Mapping AttributeRequest to Attribute: {}", request);
        return Attribute.builder()
                .id(request.id())
                .name(request.name())
                .build();
    }

    public AttributeResponse toResponse(@NonNull Attribute attribute) {
        log.debug("Mapping Attribute to AttributeResponse: {}", attribute);
        return AttributeResponse.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .values(this.mapAttributeValuesFor(attribute))
                .build();
    }

    private List<AttributeValueResponse> mapAttributeValuesFor(Attribute attribute) {
        return Optional.ofNullable(attribute.getValues())
                .map(attributeValues -> attributeValues.stream()
                        .map(attributeValueMapper::toResponse)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
