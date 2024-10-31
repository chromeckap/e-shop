package eshop.backend.service.impl;

import eshop.backend.exception.ProductNotFoundException;
import eshop.backend.exception.VariantNotFoundException;
import eshop.backend.model.Product;
import eshop.backend.model.Variant;
import eshop.backend.repository.AttributeValueRepository;
import eshop.backend.repository.ProductRepository;
import eshop.backend.repository.VariantRepository;
import eshop.backend.request.VariantRequest;
import eshop.backend.response.VariantResponse;
import eshop.backend.service.DiscountService;
import eshop.backend.service.VariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static eshop.backend.utils.EntityUtils.findByIdOrElseThrow;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl implements VariantService {
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final DiscountService discountService;
    private final InventoryServiceImpl inventoryService;

    @Override
    public Variant createVariant(VariantRequest request) throws ProductNotFoundException {
        var product = findByIdOrElseThrow(request.id(), productRepository, ProductNotFoundException::new);
        var variant = new Variant(request);

        variant.setProduct(product);
        manageAttributeValuesIfExist(variant, request);

        return variantRepository.save(variant);
    }

    @Override
    public Variant updateVariant(VariantRequest request) throws VariantNotFoundException {
        var variant = findByIdOrElseThrow(request.id(), variantRepository, VariantNotFoundException::new);

        updateVariantProperties(variant, request);
        manageAttributeValuesIfExist(variant, request);

        return variantRepository.save(variant);
    }

    @Override
    public VariantResponse getVariant(Long variantId) throws VariantNotFoundException {
        var variant = findByIdOrElseThrow(variantId, variantRepository, VariantNotFoundException::new);

        var standardPrice = variant.getBasePrice();
        var priceAfterDiscount = discountService.calculateDiscountedPrice(variant);
        var discountedPercentage = (int) ((1 - (priceAfterDiscount.divide(standardPrice, 2, RoundingMode.HALF_UP).doubleValue())) * 100);
        var isAvailable = inventoryService.isVariantAvailable(variant);
        var quantity = inventoryService.getTotalQuantity(variant);

        return VariantResponse.builder()
                .basePrice(standardPrice)
                .discountedPrice(priceAfterDiscount)
                .discountPercentage(discountedPercentage)
                .isAvailable(isAvailable)
                .quantity(quantity)
                .build();
    }

    @Override
    public void deleteVariant(Long variantId) throws VariantNotFoundException {
        var variant = findByIdOrElseThrow(variantId, variantRepository, VariantNotFoundException::new);

        variantRepository.delete(variant);
    }

    @Override //todo delete or is this method necessary?
    public List<Variant> listOfVariants(Sort.Direction direction, String attribute) {
        return variantRepository.findAll(Sort.by(direction, attribute));
    }

    @Override
    public Set<VariantResponse> listByProduct(Product product) {
        return product.getVariants().stream()
                .map(variant -> {
                    try {
                        return getVariant(variant.getId());
                    } catch (VariantNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
    }

    private void manageAttributeValuesIfExist(Variant variant, VariantRequest request) {
        var isAttributeValuesNotEmpty = request.attributeValueIds().isEmpty();

        if (isAttributeValuesNotEmpty) {
            var attributeValues = attributeValueRepository.findAllById(request.attributeValueIds());
            variant.setValues(new HashSet<>(attributeValues));
        }
    }

    private void updateVariantProperties(Variant variant, VariantRequest request) {
        variant.setBasePrice(request.basePrice());
        variant.setDiscountedPrice(request.discountedPrice());
    }
}
