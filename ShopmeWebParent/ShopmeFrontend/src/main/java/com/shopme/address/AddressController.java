package com.shopme.address;

import com.shopme.Utility;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AddressController {
    private final AddressService addressService;
    private final CustomerService customerService;

    @Autowired
    public AddressController(AddressService addressService, CustomerService customerService) {
        this.addressService = addressService;
        this.customerService = customerService;
    }

    @GetMapping("/address_book")
    public String showAddressBook(HttpServletRequest request, Model model) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.listAddressBook(authenticatedCustomer);

        boolean usePrimaryAddressAsDefault = true;
        for(Address address : listAddresses) {
            if(address.isDefaultForShipping()) {
                usePrimaryAddressAsDefault = false;
                break;
            }
        }
        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("listAddresses", listAddresses);
        model.addAttribute("customer", authenticatedCustomer);
        return "address_book/addresses";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findCustomerByEmail(email);
    }

    @GetMapping("/address_book/new")
    public String newAddress(Model model) {
        Address address = new Address();
        List<Country> listCountries = addressService.listCountries();
        model.addAttribute("address", address);
        model.addAttribute("listCountries", listCountries);
        return "address_book/address_form";
    }

    @PostMapping("/address_book/save")
    public String saveAddress(Address addressInForm,HttpServletRequest request, RedirectAttributes ra) {
        if(addressInForm.getCustomer() == null) {
            Customer authenticatedCustomer = getAuthenticatedCustomer(request);
            addressInForm.setCustomer(authenticatedCustomer);
        }
        ra.addFlashAttribute("message", String.format("The address has been %s", addressInForm.getId() != null ? "updated" : "created"));
        addressService.save(addressInForm);

        return "redirect:/address_book";
    }

    @GetMapping("/address_book/edit/{id}")
    public String editAddress(@PathVariable(name = "id") Integer id, HttpServletRequest request ,RedirectAttributes ra, Model model) {
         Customer authenticatedCustomer = getAuthenticatedCustomer(request);
         Address address = addressService.get(id, authenticatedCustomer.getId());
         List<Country> listCountries = addressService.listCountries();
         model.addAttribute("listCountries", listCountries);
         model.addAttribute("address", address);
         model.addAttribute("pageTitle", String.format("Edit Address (ID: %d)", id));
         return "address_book/address_form";
    }

    @GetMapping("/address_book/delete/{id}")
    public String deleteAddress(@PathVariable(name = "id") Integer id, HttpServletRequest request, RedirectAttributes ra) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        addressService.delete(id, authenticatedCustomer.getId());
        ra.addFlashAttribute("message", String.format("The address with ID %d has been deleted", id));
        return "redirect:/address_book";
    }

    @GetMapping("/address_book/default/{id}")
    public String setDefaultAddress(@PathVariable(name = "id") Integer id, HttpServletRequest request) {
        Customer authenticatedCustomer = getAuthenticatedCustomer(request);
        addressService.setDefaultAddress(id, authenticatedCustomer.getId());
        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/address_book";
        if(redirectOption.equals("cart")) {
            redirectURL = "redirect:/cart";
        }
        return redirectURL;
    }
}
