package eshop.backend.service;

import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.exception.VariantNotFoundException;
import eshop.backend.model.Product;
import eshop.backend.model.Variant;
import eshop.backend.request.VariantRequest;
import eshop.backend.response.VariantResponse;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public interface VariantService {
    Variant createVariant(VariantRequest request) throws ProductNotFoundException;
    VariantResponse getVariant(Long variantId) throws VariantNotFoundException;
    Variant updateVariant(VariantRequest request) throws VariantNotFoundException;
    void deleteVariant(Long variantId) throws VariantNotFoundException;
    List<Variant> listOfVariants(Sort.Direction direction, String attribute);

    Set<VariantResponse> listByProduct(Product product);
}
