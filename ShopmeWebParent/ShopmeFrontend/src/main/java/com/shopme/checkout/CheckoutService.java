package com.shopme.checkout;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {

    public static final int DIM_DIVISOR = 139;

    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
            CheckoutInfo checkoutInfo = new CheckoutInfo();
            float productCost = calculateProductCost(cartItems);
            float productTotal = calculateProductTotal(cartItems);
            float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
            float paymentTotal = productTotal + shippingCostTotal;

            checkoutInfo.setProductCost(productCost);
            checkoutInfo.setProductTotal(productTotal);
            checkoutInfo.setShippingCostTotal(shippingCostTotal);
            checkoutInfo.setPaymentTotal(paymentTotal);


            checkoutInfo.setDeliverDays(shippingRate.getDays());
            checkoutInfo.setCodSupported(shippingRate.getCodSupported());




            return checkoutInfo;
    }

    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
         float shippingCostTotal = 0.0f;
         for(CartItem item : cartItems) {
             Product product = item.getProduct();
             float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
             float finalWeight = Math.max(product.getWeight(), dimWeight);
             float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();
             item.setShippingCost(shippingCost);
             shippingCostTotal += shippingCost;
         }
         return shippingCostTotal;
    }

    private float calculateProductTotal(List<CartItem> cartItems) {
        return cartItems.stream().map(CartItem::getSubTotal).reduce(0f, Float::sum);
    }

    private float calculateProductCost(List<CartItem> cartItems) {
        return cartItems.stream().map(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getCost()).reduce(0.0f, Float::sum);
    }



}
