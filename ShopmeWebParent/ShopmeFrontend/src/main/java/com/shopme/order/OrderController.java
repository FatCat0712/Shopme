package com.shopme.order;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.customer.CustomerService;
import com.shopme.security.CustomerDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.shopme.order.OrderService.ORDER_PER_PAGE;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final CustomerService customerService;

    @Autowired
    public OrderController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @GetMapping("/orders")
    public String listFirstPage() {
        return "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(
            @AuthenticationPrincipal CustomerDetails customerDetails,
            @PathVariable("pageNum") Integer pageNum,
            @RequestParam(name = "sortField") String sortField,
            @RequestParam(name = "sortDir") String sortDir,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model
    ) {
        String customerEmail = customerDetails.getUsername();
        Customer customer = customerService.findCustomerByEmail(customerEmail);
        Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir, keyword);

        List<Order> listOrders = page.getContent();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        int startCount = (pageNum - 1) * ORDER_PER_PAGE + 1;
        long endCount = startCount + ORDER_PER_PAGE - 1;

        if(endCount > totalItems) {
            endCount = totalItems;
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("listOrders", listOrders);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reversedSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("moduleURL", "/orders");

        return "order/orders_customer";
    }
}
