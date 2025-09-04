package com.shopme.admin.shippingrate;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.country.Country;
import com.shopme.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ShippingRateController {
    private final ShippingRateService shippingRateService;

    private final String defaultRedirectUrl = "redirect:/shipping_rates/page/1?sortField=country.name&sortDir=asc";

    @Autowired
    public ShippingRateController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @GetMapping("/shipping_rates")
    public String listFirstPage() {
        return defaultRedirectUrl;
    }

    @GetMapping("/shipping_rates/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleURL = "/shipping_rates", listName = "listShippingRates") PagingAndSortingHelper helper,
            @PathVariable("pageNum") Integer pageNum
            )
    {
        shippingRateService.listByPage(pageNum, helper);
        return "shipping_rates/shipping_rates";
    }

    @GetMapping("/shipping_rates/new")
    public String newRate(Model model) {
        ShippingRate shippingRate = new ShippingRate();
        List<Country> listCountries = shippingRateService.listCountries();
        model.addAttribute("pageTitle", "New Shipping Rate");
        model.addAttribute("shippingRate", shippingRate);
        model.addAttribute("listCountries", listCountries);
        return "shipping_rates/shipping_rate_form";
    }

    @PostMapping("/shipping_rates/save")
    public String saveRate(ShippingRate shippingRateInForm, RedirectAttributes ra) {
        Integer id = shippingRateInForm.getId();
        Country country = shippingRateInForm.getCountry();
        String state = shippingRateInForm.getState();

        try {
            shippingRateService.checkUnique(id, country, state);
            shippingRateService.save(shippingRateInForm);
            ra.addFlashAttribute("message", String.format("The shipping rate with ID %d has been %s", id , id != null ? "updated" : "created"));
        } catch (ShippingRateAlreadyExistsException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return defaultRedirectUrl;
    }

    @GetMapping("/shipping_rates/edit/{id}")
    public String editRate(@PathVariable(name = "id") Integer id, RedirectAttributes ra, Model model) {
        try {
            ShippingRate shippingRate = shippingRateService.get(id);
            List<Country> listCountries = shippingRateService.listCountries();
            model.addAttribute("shippingRate", shippingRate);
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("pageTitle", String.format("Edit Rate (ID: %d)", id));
            return "shipping_rates/shipping_rate_form";
        } catch (ShippingRateNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return defaultRedirectUrl;
        }
    }

    @GetMapping("/shipping_rates/cod/{id}/enabled/{status}")
    public String updateCODSupport(@PathVariable(name = "id") Integer id, @PathVariable(name = "status") Boolean status, RedirectAttributes ra) {
        try {
            shippingRateService.updateCODSupport(id, status);
            ra.addFlashAttribute("message", String.format("COD Support for Shipping rate with ID %d has been %s", id, status ? "enabled" : "disabled"));
        } catch (ShippingRateNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultRedirectUrl;
    }

    @GetMapping("/shipping_rates/delete/{id}")
    public String deleteRate(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
        try {
            shippingRateService.delete(id);
            ra.addFlashAttribute("message", String.format("The shipping rate with ID %d has been deleted", id));
        } catch (ShippingRateNotFoundException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return defaultRedirectUrl;
    }




}
