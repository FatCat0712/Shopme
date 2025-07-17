package com.shopme.admin.customer;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional

public class CustomerService  {
    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public final static int CUSTOMER_PER_PAGE = 10;

    public List<Customer> listAll() {
        return (List<Customer>) customerRepository.findAll(Sort.by("id").ascending());
    }


    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, CUSTOMER_PER_PAGE, customerRepository);
    }

    public void enableCustomer(Integer id, Boolean status) throws CustomerNotFound {
        Optional<Customer> savedCustomer = customerRepository.findById(id);
        if(savedCustomer.isEmpty()) {
            throw new CustomerNotFound("Could not find customer with id: " + id);
        }
        else {
            savedCustomer.get().setEnabled(status);
            customerRepository.save(savedCustomer.get());
        }
    }

    public Customer getCustomerById(Integer id) throws CustomerNotFound {
        Optional<Customer> savedCustomer = customerRepository.findById(id);
        if(savedCustomer.isEmpty()) {
            throw new CustomerNotFound("Could not find customer with id: " + id);
        }
        else {
           return savedCustomer.get();
        }
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public Customer save(Customer customer) throws CustomerNotFound {
        String rawPassword = customer.getPassword();
        Customer savedCustomer = customerRepository.findById(customer.getId()).orElseThrow(() -> new CustomerNotFound("Customer not found"));
        if(rawPassword != null && !rawPassword.isEmpty()) {
           customer.setPassword(passwordEncoder.encode(rawPassword));
        }
        else {
            customer.setPassword(savedCustomer.getPassword());
        }
        customer.setCreatedTime(savedCustomer.getCreatedTime());
        customer.setEnabled(savedCustomer.isEnabled());

        return customerRepository.save(customer);
    }

    public boolean isEmailUnique(Integer id, String email) {
        Customer customerByEmail = customerRepository.findByEmail(email);

        if(customerByEmail == null) return true;

        return customerByEmail.getId().equals(id);
    }

    public void deleteCustomer(Integer id) throws CustomerNotFound {
        Optional<Customer> savedCustomer = customerRepository.findById(id);
        if(savedCustomer.isEmpty()) {
            throw new CustomerNotFound("Could not find customer with id: " + id);
        }
        else {
            customerRepository.deleteById(id);
        }
    }
}
