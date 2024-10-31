package eshop.backend.service;

import eshop.backend.exception.DetailSectionNotFoundException;
import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.model.DetailSection;
import eshop.backend.request.DetailSectionRequest;

public interface DetailSectionService {
    DetailSection createDetailSection(DetailSectionRequest request) throws ProductNotFoundException;
    DetailSection updateDetailSection(DetailSectionRequest request) throws DetailSectionNotFoundException;
    DetailSection getDetailSection(Long detailSectionId) throws DetailSectionNotFoundException;
    void deleteDetailSection(Long detailSectionId) throws DetailSectionNotFoundException;
}
