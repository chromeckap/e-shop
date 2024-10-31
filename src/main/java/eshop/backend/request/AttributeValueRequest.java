package eshop.backend.request;

public record AttributeValueRequest(
        Long id,
        String value,
        Long attributeId
) {}
