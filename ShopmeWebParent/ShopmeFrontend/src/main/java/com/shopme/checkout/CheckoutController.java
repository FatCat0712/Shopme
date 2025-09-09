package com.shopme.checkout;

import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.checkout.paypal.PayPalApiException;
import com.shopme.checkout.paypal.PaypalService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;
import com.shopme.setting.PaymentSettingBag;
import com.shopme.setting.SettingService;
import com.shopme.shippingrate.ShippingRateService;
import com.shopme.shoppingcart.ShoppingCartService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final CustomerService customerService;
    private final AddressService addressService;
    private final ShippingRateService shipService;
    private final ShoppingCartService cartService;
    private  final OrderService orderService;
    private final SettingService settingService;
    private final PaypalService paypalService;


    @Autowired
    public CheckoutController(
            CheckoutService checkoutService, CustomerService customerService, AddressService addressService,
            ShippingRateService shipService, ShoppingCartService cartService, OrderService orderService,
            SettingService settingService, PaypalService paypalService
    ) {
        this.checkoutService = checkoutService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shipService = shipService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.settingService = settingService;
        this.paypalService = paypalService;
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request){
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findCustomerByEmail(email);
    }

    @GetMapping("/checkout")
    private String showCheckoutPage(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if(defaultAddress != null) {
            model.addAttribute("shippingAddress", defaultAddress.toString());
            shippingRate = shipService.getShippingRateForAddress(defaultAddress);
        }
        else {
            model.addAttribute("shippingAddress", customer.toString());
            shippingRate = shipService.getShippingRateForCustomer(customer);
        }

        if(shippingRate == null) {
            return "redirect:/cart";
        }

        List<CartItem> cartItems = cartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);
        String currencyCode = settingService.getCurrencyCode();
        PaymentSettingBag paymentSettingBag = settingService.getPaymentSettings();

        String paypalClientId = paymentSettingBag.getClientID();


        model.addAttribute("paypalClientId", paypalClientId);
        model.addAttribute("customer", customer);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);

        return "checkout/checkout";
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request, Model model) throws MessagingException, UnsupportedEncodingException {
        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        if(defaultAddress != null) {
            shippingRate = shipService.getShippingRateForAddress(defaultAddress);
        }
        else {
            shippingRate = shipService.getShippingRateForCustomer(customer);
        }

        List<CartItem> cartItems = cartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

//        store order information into database
        Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);

//        remove all items in shopping cart
        cartService.deleteByCustomer(customer);

        Integer cartQuantity = cartService.fetchCartQuantityById(customer);


        HttpSession session = request.getSession(false);
        session.setAttribute("sum", cartQuantity);

        model.addAttribute("listCartItems", List.of());



//        send order confirmation email
        sendOrderConfirmationEmail(request, createdOrder);

        return "checkout/order_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request, Order createdOrder) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettings = settingService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = createdOrder.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        subject = subject.replace("[[orderId]]", String.valueOf(createdOrder.getId()));

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());

        helper.setTo(toAddress);

        helper.setSubject(subject);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
        String orderTime = dateFormat.format(createdOrder.getOrderTime());

        CurrencySettingBag currencySettings = settingService.getCurrencySettings();

//        format currency for total amount in email based on currency settings
        String totalAmount = Utility.formatCurrency(createdOrder.getTotal(), currencySettings);

        content = content.replace("[[name]]", createdOrder.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(createdOrder.getId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[shippingAddress]]", createdOrder.getShippingAddress());
        content = content.replace("[[total]]", totalAmount);
        content = content.replace("[[paymentMethod]]", createdOrder.getPaymentMethod().toString());

        helper.setText(content, true);

        mailSender.send(message);



    }

    @PostMapping("/process_paypal_order")
    public String processPayPalOrder(HttpServletRequest request, Model model) throws MessagingException, UnsupportedEncodingException {
        String orderId = request.getParameter("orderId");
        String pageTitle;
        String message;
        try {
            if(paypalService.validateOrder(orderId)) {
                return placeOrder(request, model);
            }
            else {
                pageTitle = "Checkout Failure";
                message = "ERROR: Transaction could not be completed because order information is invalid";
            }
        } catch (PayPalApiException e) {
            pageTitle = "Checkout Failure";
            message = "ERROR: Transaction failed due to error: " + e.getMessage();
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("message", message);
        return "message";
    }


}
