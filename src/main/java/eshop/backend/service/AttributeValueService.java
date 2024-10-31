package eshop.backend.service;

import eshop.backend.exception.AttributeNotFoundException;
import eshop.backend.exception.AttributeValueNotFoundException;
import eshop.backend.model.AttributeValue;
import eshop.backend.request.AttributeValueRequest;

import java.util.List;

public interface AttributeValueService {
    AttributeValue createAttributeValue(AttributeValueRequest request) throws AttributeNotFoundException;
    AttributeValue getAttributeValue(Long attributeValueId) throws AttributeValueNotFoundException;
    AttributeValue updateAttributeValue(AttributeValueRequest request) throws AttributeValueNotFoundException;
    void deleteAttributeValue(Long attributeValueId) throws AttributeValueNotFoundException;
    List<AttributeValue> listByAttributeId(Long attributeId) throws AttributeNotFoundException;
}