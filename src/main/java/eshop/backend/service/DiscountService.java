package eshop.backend.service;

import eshop.backend.exception.DiscountNotFoundException;
import eshop.backend.model.Discount;
import eshop.backend.model.Variant;
import eshop.backend.request.DiscountRequest;

import java.math.BigDecimal;

public interface DiscountService {
    Discount create(DiscountRequest request);
    Discount read(Long discountId) throws DiscountNotFoundException;
    Discount update(DiscountRequest request) throws DiscountNotFoundException;
    void delete(Long discountId) throws DiscountNotFoundException;
    BigDecimal calculateDiscountedPrice(Variant variant);

}
