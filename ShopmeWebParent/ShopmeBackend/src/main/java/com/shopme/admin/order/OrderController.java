package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.setting.Setting;
import com.shopme.common.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final SettingService settingService;

    private final String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

    @Autowired
    public OrderController(OrderService orderService, SettingService settingService) {
        this.orderService = orderService;
        this.settingService = settingService;
    }

    @GetMapping("/orders")
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleURL = "/orders", listName = "listOrders") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") Integer pageNum,
            HttpServletRequest request,
            @AuthenticationPrincipal ShopmeUserDetails loggedUser
            ) {
        orderService.listByPage(pageNum, helper);
        loadCurrencySettings(request);

        if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
            return "orders/orders_shipper";
        }

        return "orders/orders";
    }

    private void loadCurrencySettings(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        currencySettings.forEach(setting -> request.setAttribute(setting.getKey(), setting.getValue()));
    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(
            @PathVariable(name = "id") Integer id,
            HttpServletRequest request,
            Model model, RedirectAttributes ra,
            @AuthenticationPrincipal ShopmeUserDetails loggedUser
    ) {
         try {
             Order order =orderService.get(id);
             loadCurrencySettings(request);

             boolean isVisibleForAdminOrSalesperson = false;

             if(loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
                 isVisibleForAdminOrSalesperson = true;
             }

             model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
             model.addAttribute("order", order);
             return "orders/order_details_modal";
         }catch (OrderNotFoundException e) {
             ra.addFlashAttribute("errorMessage", e.getMessage());
             return defaultRedirectURL;
         }
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
        try {
            orderService.delete(id);
            ra.addFlashAttribute("message", "The order has been deleted");
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Order order = orderService.get(id);
            order.getOrderTracks().sort(Comparator.comparing(OrderTrack::getUpdatedTime));
            List<Country> listCountries = orderService.listAllCountries();
            model.addAttribute("pageTitle", String.format("Edit Order (ID: %d)", order.getId()));
            model.addAttribute("order", order);
            model.addAttribute("listCountries", listCountries);
            return "orders/order_form";
        } catch (OrderNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @PostMapping("/orders/save")
    public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
        updateProductDetails(order, request);
        updateOrderTracks(order, request);
        orderService.save(order);
        ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated");
        return defaultRedirectURL;
    }

    private void updateProductDetails(Order order, HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShippingCosts = request.getParameterValues("shippingCost");

        Set<OrderDetail> orderDetails = order.getOrderDetails();

        for(int i = 0; i < detailIds.length; i++) {
            OrderDetail orderDetail = new OrderDetail();
            int detailId = Integer.parseInt(detailIds[i]);
            if(detailId > 0) {
                orderDetail.setId(detailId);
            }
            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShippingCosts[i]));

            orderDetails.add(orderDetail);
        }
        order.setOrderDetails(orderDetails);

    }

    private void updateOrderTracks(Order order, HttpServletRequest request)  {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackStatus = request.getParameterValues("trackStatus");
        String[] trackNotes = request.getParameterValues("trackNote");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        List<OrderTrack> orderTracks = order.getOrderTracks();
        for(int i = 0; i < trackIds.length; i++) {
            OrderTrack orderTrack = new OrderTrack();
            int trackId = Integer.parseInt(trackIds[i]);
            if(trackId > 0) {
                orderTrack.setId(trackId);
            }
            orderTrack.setOrder(order);
            orderTrack.setStatus(OrderStatus.valueOf(trackStatus[i].split("\\|")[1]));
            orderTrack.setNotes(trackNotes[i]);
            try {
                orderTrack.setUpdatedTime(dateFormatter.parse(trackDates[i]));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            orderTracks.add(orderTrack);

        }
        order.setOrderStatus(orderTracks.getLast().getStatus());
        order.setOrderTracks(orderTracks);

    }




}
