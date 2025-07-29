package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
            HttpServletRequest request
    ) {
        orderService.listByPage(pageNum, helper);
        loadCurrencySettings(request);
        return "orders/orders";
    }

    private void loadCurrencySettings(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        currencySettings.forEach(setting -> request.setAttribute(setting.getKey(), setting.getValue()));
    }

    @GetMapping("/orders/detail/{id}")
    public String viewOrderDetails(@PathVariable(name = "id") Integer id, HttpServletRequest request, Model model, RedirectAttributes ra) {
         try {
             Order order =orderService.get(id);
             loadCurrencySettings(request);
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




}
