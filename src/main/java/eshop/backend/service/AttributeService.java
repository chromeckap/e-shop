package eshop.backend.service;

import eshop.backend.exception.AttributeNotFoundException;
import eshop.backend.model.Attribute;
import eshop.backend.request.AttributeRequest;

import java.util.List;

public interface AttributeService {
    Attribute createAttribute(AttributeRequest attribute);
    Attribute getAttribute(Long attributeId) throws AttributeNotFoundException;
    Attribute updateAttribute(AttributeRequest attribute) throws AttributeNotFoundException;
    void deleteAttribute(Long attributeId) throws AttributeNotFoundException;
    List<Attribute> listOfAttributes();
}
