package com.shopme.admin.order;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final SettingService settingService;

    private String defaultRedirecURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

    @Autowired
    public OrderController(OrderService orderService, SettingService settingService) {
        this.orderService = orderService;
        this.settingService = settingService;
    }

    @GetMapping("/orders")
    public String listFirstPage() {
        return defaultRedirecURL;
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




}
