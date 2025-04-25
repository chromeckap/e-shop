package com.ecommerce.variant.purchase;

import com.ecommerce.variant.Variant;
import com.ecommerce.variant.VariantMapper;
import com.ecommerce.variant.VariantRepository;
import com.ecommerce.variant.VariantValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {
    private final VariantRepository variantRepository;
    private final VariantMapper variantMapper;
    private final VariantValidator variantValidator;


    private static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(100_000.0);

    /**
     * Processes the purchase of variants.
     *
     * @param request the set of purchase requests
     * @return a set of purchase responses
     */
    @Transactional
    public Set<PurchaseResponse> purchaseVariants(Set<PurchaseRequest> request) {
        variantValidator.validateSetNotEmpty(request);

        log.debug("Processing purchase request for {} variants.", request.size());

        Map<Long, PurchaseRequest> variantRequests = request.stream()
                .collect(Collectors.toMap(PurchaseRequest::id, Function.identity()));

        Set<Long> RequestVariantIds = variantRequests.keySet();
        variantValidator.validateAllVariantsExist(RequestVariantIds);

        List<Variant> existingVariants = variantRepository.findAllById(RequestVariantIds);

        variantValidator.validateTotalPriceIsNotOver(existingVariants, variantRequests, TOTAL_PRICE);

        Set<PurchaseResponse> response = existingVariants.stream().map(variant -> {
                    PurchaseRequest currentRequest = variantRequests.get(variant.getId());

                    if (!variant.isQuantityUnlimited()) {
                        variant.setQuantity(this.calculateUpdatedQuantity(variant, currentRequest));
                    }
                    return variantMapper.toPurchaseResponse(variant, currentRequest.quantity());
                })
                .collect(Collectors.toSet());
        
        variantRepository.saveAll(existingVariants);
        log.info("Purchase transaction completed successfully for {} variants.", response.size());
        return response;
    }

    /**
     * Calculates the updated quantity for a variant after a purchase.
     *
     * @param variant the variant entity
     * @param request the purchase request
     * @return the new quantity
     */
    private int calculateUpdatedQuantity(Variant variant, PurchaseRequest request) {
        variantValidator.validateAvailableQuantity(variant, request);
        int newQuantity = variant.getQuantity() - request.quantity();
        log.debug("Updated quantity for variant ID {}: new quantity {}", variant.getId(), newQuantity);
        return newQuantity;
    }
}
