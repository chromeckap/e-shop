package eshop.backend.service.impl;

import eshop.backend.exception.DetailSectionNotFoundException;
import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.model.DetailSection;
import eshop.backend.model.Product;
import eshop.backend.repository.DetailSectionRepository;
import eshop.backend.repository.ProductRepository;
import eshop.backend.request.DetailSectionRequest;
import eshop.backend.service.DetailSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static eshop.backend.utils.EntityUtils.findByIdOrElseThrow;

@Service
@RequiredArgsConstructor
public class DetailSectionServiceImpl implements DetailSectionService {
    private final DetailSectionRepository detailSectionRepository;
    private final ProductRepository productRepository;


    @Override
    public DetailSection createDetailSection(DetailSectionRequest request) throws ProductNotFoundException {
        Product product = findByIdOrElseThrow(request.productId(), productRepository, ProductNotFoundException::new);
        DetailSection detailSection = new DetailSection(request);

        detailSection.setProduct(product);

        return detailSectionRepository.save(detailSection);
    }

    @Override
    public DetailSection updateDetailSection(DetailSectionRequest request) throws DetailSectionNotFoundException {
        var detailSection = findByIdOrElseThrow(request.id(), detailSectionRepository, DetailSectionNotFoundException::new);

        updateDetailSectionProperties(detailSection, request);

        detailSectionRepository.save(detailSection);

        return detailSection;
    }

    @Override
    public DetailSection getDetailSection(Long detailSectionId) throws DetailSectionNotFoundException {
        return findByIdOrElseThrow(detailSectionId, detailSectionRepository, DetailSectionNotFoundException::new);
    }

    @Override
    public void deleteDetailSection(Long detailSectionId) throws DetailSectionNotFoundException {
        var detailSection = findByIdOrElseThrow(detailSectionId, detailSectionRepository, DetailSectionNotFoundException::new);

        detailSectionRepository.delete(detailSection);
    }

    private void updateDetailSectionProperties(DetailSection detailSection, DetailSectionRequest request) {
        detailSection.setTitle(request.title());
        detailSection.setDescription(request.description());
    }
}
