package eshop.backend.service.impl;

import eshop.backend.model.CartItem;
import eshop.backend.model.Coupon;
import eshop.backend.repository.CouponRepository;
import eshop.backend.request.CouponRequest;
import eshop.backend.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    @Override
    public Coupon create(CouponRequest request) {
        if (couponRepository.existsByCode(request.code()))
            throw new RuntimeException();

        Coupon coupon = new Coupon(request);
        return couponRepository.save(coupon);
    }

    @Override
    public Coupon readByCode(String code) {
        return couponRepository.findByCode(code).orElseThrow();
    }

    @Override
    public Coupon update(CouponRequest request) {
        var coupon = readByCode(request.code());

        updateCouponProperties(coupon, request);

        return couponRepository.save(coupon);
    }

    @Override
    public void delete(String code) {
        var coupon = readByCode(code);

        couponRepository.delete(coupon);
    }

    @Override
    public BigDecimal applyCouponIfSet(CartItem cartItem, String code) {
        if (code == null || code.isEmpty()) {
            return cartItem.getTotalPrice();
        }

        var coupon = readByCode(code);
        BigDecimal originalPrice = cartItem.getTotalPrice();
        BigDecimal discountAmount =
                switch (coupon.getType()) {
            case PERCENTAGE -> originalPrice.multiply(coupon.getDiscount()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            case FIXED_AMOUNT -> coupon.getDiscount();
        };

        BigDecimal newPrice = originalPrice.subtract(discountAmount);
        return cartItem.getTotalPrice().add(newPrice);
    }

    private void updateCouponProperties(Coupon coupon, CouponRequest request) {
        coupon.setCode(request.code());
        coupon.setType(request.type());
        coupon.setDiscount(request.discount());
        coupon.setUses(request.uses());
        coupon.setExpirationDate(request.expirationDate());
    }
}
