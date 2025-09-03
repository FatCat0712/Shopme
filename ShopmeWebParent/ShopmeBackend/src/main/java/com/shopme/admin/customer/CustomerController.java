package com.shopme.admin.customer;

import com.shopme.admin.customer.export.CustomerCsvExporter;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.country.Country;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public String listFirstPage() {
        return  "redirect:/customers/page/1?sortField=firstName&sortDir=asc";
    }

    @GetMapping("/customers/page/{pageNum}")
    public String listByPage(
            @PathVariable(name = "pageNum") int pageNum,
            @PagingAndSortingParam(moduleURL = "/customers", listName = "listCustomers")PagingAndSortingHelper helper
            ) {
        customerService.listByPage(pageNum, helper);


        return "customers/customers";

    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer, RedirectAttributes ra) throws CustomerNotFound {
            customerService.save(customer);
            ra.addFlashAttribute("message", String.format("The customer with ID %d has been updated successfully", customer.getId()));
            return "redirect:/customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable(name = "id") Integer id,Model model, RedirectAttributes ra) {
        try {
            Customer savedCustomer = customerService.getCustomerById(id);
            List<Country> listCountries = customerService.listAllCountries();
            model.addAttribute("customer", savedCustomer);
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", savedCustomer.getId()));
            return "customers/customer_form";
        }catch (CustomerNotFound ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customers/customers";
        }
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable("id") Integer id,HttpServletRequest request, RedirectAttributes ra) {
        try {
            customerService.deleteCustomer(id);
            ra.addFlashAttribute("message", String.format("The customer with ID %d has been deleted successfully", id));
            String referer = request.getHeader("referer");
            return "redirect:" + referer;
        }catch (CustomerNotFound ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customers/customers";
        }
    }


    @GetMapping("/customers/{id}/enabled/{status}")
    public String enableCustomer(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "status") Boolean status,
            HttpServletRequest request,
            RedirectAttributes ra) {
        try {
            customerService.enableCustomer(id, status);
            ra.addFlashAttribute("message", String.format("The customer with ID %d has been %s successfully", id, status ? "enabled" : "disabled"));
            String referer = request.getHeader("referer");
            return "redirect:" + referer;
        }catch (CustomerNotFound ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customers/customers";
        }

    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomerDetails(
            @PathVariable(name = "id") Integer id,
            Model model,
            RedirectAttributes ra) {
        try {
            Customer savedCustomer = customerService.getCustomerById(id);
            model.addAttribute("customer", savedCustomer);
            return "customers/customer_detail_modal";
        }catch (CustomerNotFound ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/customers";
        }
    }

    @GetMapping("/customers/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        List<Customer> listCustomers = customerService.listAll();
       CustomerCsvExporter exporter = new CustomerCsvExporter();
        exporter.export(listCustomers, response);
    }


}
