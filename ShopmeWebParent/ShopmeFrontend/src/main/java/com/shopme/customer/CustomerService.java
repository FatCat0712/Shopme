package com.shopme.customer;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.country.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CustomerService{
    private final CountryRepository countryRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public CustomerService(CountryRepository countryRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.countryRepository = countryRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findByEmail(email);
        return customer == null;
    }
    
    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);


        customerRepository.save(customer);
    }

    public Customer update(Customer customer) throws CustomerNotFoundException {
        Customer savedCustomer = customerRepository.findById(customer.getId()).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        if(savedCustomer.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
            String rawPassword = customer.getPassword();
            if(rawPassword != null && !rawPassword.isEmpty()) {
                customer.setPassword(passwordEncoder.encode(rawPassword));
            }
            else {
                customer.setPassword(savedCustomer.getPassword());
            }
        }
        else {
            customer.setPassword(passwordEncoder.encode(savedCustomer.getPassword()));
        }
        customer.setCreatedTime(savedCustomer.getCreatedTime());
        customer.setEnabled(savedCustomer.isEnabled());
        customer.setAuthenticationType(savedCustomer.getAuthenticationType());
        customer.setResetPasswordToken(savedCustomer.getResetPasswordToken());

        return customerRepository.save(customer);
    }

    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    private void encodePassword(Customer customer) {
        String encryptedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encryptedPassword);

    }

    public boolean verify(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);

        if(customer == null || customer.isEnabled()) return false;
        else {
            customerRepository.enabled(customer.getId());
            return true;
        }
    }

    public void updateAuthenticationType(Customer customer, AuthenticationType type) {
        if(!customer.getAuthenticationType().equals(type)) {
            customerRepository.updateAuthenticationType(customer.getId(), type);
        }
    }

    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode, AuthenticationType authenticationType) {
        Customer customer = new Customer();
        customer.setEmail(email);

        setName(name, customer);
        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(authenticationType);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostalCode("");
        customer.setCountry(countryRepository.findByCode(countryCode));

        customerRepository.save(customer);
    }

    private void setName(String name, Customer customer) {

        String[] nameArray = name.trim().split(" ");
        if(nameArray.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        }
        else {
            String firstName = nameArray[0];
            customer.setFirstName(firstName);

            String lastName = name.replaceFirst(firstName + " ", "");
            customer.setLastName(lastName);

        }
    }

    public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email);
        if(customer != null) {
            String token = RandomString.make(30);
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);
            return token;
        }
        else {
            throw new CustomerNotFoundException("Could not find any customer with the email: " + email);
        }
    }

    public Customer getCustomerByResetPasswordToken(String token) {
        return customerRepository.findByResetPasswordToken(token);
    }

    public void updatePassword(String token, String password) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByResetPasswordToken(token);
        if(customer != null) {
            if(!password.isEmpty()) {
                customer.setPassword(password);
                customer.setResetPasswordToken(null);
                encodePassword(customer);
                customerRepository.save(customer);
            }
        }else {
            throw new CustomerNotFoundException("Customer not found. Invalid Token");
        }

    }


}


