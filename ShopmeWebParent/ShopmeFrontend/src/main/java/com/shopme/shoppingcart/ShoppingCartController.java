package com.shopme.shoppingcart;

import com.shopme.ControllerHelper;
import com.shopme.address.AddressService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.shippingrate.ShippingRateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ShoppingCartController {
    private final ShoppingCartService cartService;
    private final ControllerHelper controllerHelper;
    private final ShippingRateService shippingRateService;
    private final AddressService addressService;

    @Autowired
    public ShoppingCartController(
            ShoppingCartService cartService, ControllerHelper controllerHelper,
            ShippingRateService shippingRateService, AddressService addressService
    ) {
        this.cartService = cartService;
        this.controllerHelper = controllerHelper;
        this.shippingRateService = shippingRateService;
        this.addressService = addressService;
    }

    @GetMapping("/cart")
    public String viewCart(HttpServletRequest request, Model model) throws CustomerNotFoundException {

        Customer customer = controllerHelper.getAuthenticatetdCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);
        Float estimatedTotal = cartItems.stream().map(CartItem::getSubTotal).reduce(0F, Float::sum);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        if(defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        }
        else {
            usePrimaryAddressAsDefault = true;
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);
        model.addAttribute("estimatedTotal", estimatedTotal);
        model.addAttribute("listCartItems", cartItems);
        return "cart/shopping_cart";
    }

    @GetMapping("/cart/update_cart_preview")
    public String updateCartPreview(HttpServletRequest request, Model model) throws CustomerNotFoundException {
        Customer customer = controllerHelper.getAuthenticatetdCustomer(request);
        HttpSession session = request.getSession(false);
        if(customer != null) {
            List<CartItem> listCartItems = cartService.listCartItems(customer);
            Integer updatedQuantity = cartService.fetchCartQuantityByCustomer(customer);
            session.setAttribute("sum", updatedQuantity);
            model.addAttribute("listCartItems", listCartItems);
        }
        return "cart/cart_fragment :: miniCart";
    }



}
