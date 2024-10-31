package eshop.backend.service;

import eshop.backend.model.CartItem;
import eshop.backend.model.Coupon;
import eshop.backend.model.OrderItem;
import eshop.backend.request.CouponRequest;

import java.math.BigDecimal;

public interface CouponService {
    Coupon create(CouponRequest request);

    Coupon readByCode(String code);
    Coupon update(CouponRequest request);
    void delete(String code);
    BigDecimal applyCouponIfSet(CartItem cartItem, String code);
}
