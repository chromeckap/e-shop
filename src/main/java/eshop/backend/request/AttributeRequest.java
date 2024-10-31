package eshop.backend.request;

import java.util.Set;

public record AttributeRequest(
        Long id,
        String name,
        Set<AttributeValueRequest> valueRequests
) {}
