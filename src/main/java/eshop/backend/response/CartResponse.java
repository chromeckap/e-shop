package eshop.backend.response;

import eshop.backend.model.Cart;
import eshop.backend.model.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class CartResponse {
    private BigDecimal totalPrice;
    private BigDecimal totalPriceIfDiscounted;
    private Set<CartItem> cartItems;

}
