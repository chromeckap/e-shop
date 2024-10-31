package eshop.backend.exception;

public class CouponOutOfUsesException extends RuntimeException {
    public CouponOutOfUsesException(Long couponId) {
        super("Coupon with ID " + couponId + " has been used up.");
    }

}