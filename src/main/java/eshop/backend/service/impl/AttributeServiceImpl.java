package eshop.backend.service.impl;

import eshop.backend.exception.AttributeNotFoundException;
import eshop.backend.model.Attribute;
import eshop.backend.repository.AttributeRepository;
import eshop.backend.request.AttributeRequest;
import eshop.backend.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static eshop.backend.utils.EntityUtils.findByIdOrElseThrow;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {
    private final AttributeRepository attributeRepository;

    @Override
    public Attribute createAttribute(AttributeRequest request) {
        var attribute = new Attribute(request);

        return attributeRepository.save(attribute);
    }

    @Override
    public Attribute getAttribute(Long attributeId) throws AttributeNotFoundException {
        return findByIdOrElseThrow(attributeId, attributeRepository, AttributeNotFoundException::new);
    }

    @Override
    public Attribute updateAttribute(AttributeRequest request) throws AttributeNotFoundException {
        var attribute = getAttribute(request.id());

        attribute.setName(request.name());

        return attributeRepository.save(attribute);
    }

    @Override
    public void deleteAttribute(Long attributeId) throws AttributeNotFoundException {
        var attribute = getAttribute(attributeId);

        attributeRepository.delete(attribute);
    }

    @Override
    public List<Attribute> listOfAttributes() {
        return attributeRepository.findAll();
    }
}