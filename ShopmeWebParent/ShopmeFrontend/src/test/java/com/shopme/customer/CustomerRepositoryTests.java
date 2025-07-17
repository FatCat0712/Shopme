package com.shopme.customer;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTests {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

//    -- 1
//            ('john.doe@example.com',
//            '$2a$10$examplehashjohn1',
//            'John', 'Doe',
//            '0123456789',
//            '123 Main St', NULL,
//            'New York',
//            1, '10001',
//    NOW(), TRUE, 'verifycode123john'),
//
//            -- 2
//            ('jane.smith@example.com',
//            '$2a$10$examplehashjane2',
//            'Jane', 'Smith',
//            '0987654321',
//            '456 Elm Street', 'Apt 7B',
//            'Los Angeles',
//            1, '90001',
//    NOW(), TRUE, 'verifycode456jane'),
//




    @Test
    public void testCreateCustomer() {
        Country country = entityManager.find(Country.class, 1);

        Customer customer1 = new Customer();
        customer1.setEmail("john.doe@example.com");
        customer1.setPassword("$2a$10$examplehashjohn1");
        customer1.setFirstName("Jane'");
        customer1.setLastName("Doe");
        customer1.setPhoneNumber("'0123456789");
        customer1.setAddressLine1("123 Main St");
        customer1.setCountry(country);
        customer1.setCity("New York");
        customer1.setPostalCode("10001");
        customer1.setCreatedTime(new Date());
        customer1.setEnabled(true);
        customer1.setVerificationCode("verifycode123john");

        Customer customer2 = new Customer();
        customer2.setEmail("jane.smith@example.com");
        customer2.setPassword("$2a$10$examplehashjane2");
        customer2.setFirstName("Jane'");
        customer2.setLastName("Smith");
        customer2.setPhoneNumber("'0987654321");
        customer2.setAddressLine1("456 Elm Street");
        customer2.setAddressLine2("Apt 7B");
        customer2.setCity("Los Angeles");
        customer2.setCountry(country);
        customer2.setPostalCode("90001");
        customer2.setCreatedTime(new Date());
        customer2.setEnabled(true);
        customer2.setVerificationCode("verifycode456jane");

        Iterable<Customer> savedCustomers = customerRepository.saveAll(List.of(customer1, customer2));

        assertThat(savedCustomers).size().isGreaterThan(0);

    }

    @Test
    public void testListCustomer() {
        Iterable<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            System.out.println(customer);
        }
        assertThat(customers).size().isGreaterThan(0);
    }

    @Test
    public void testUpdateCustomer() {
        Customer customer = entityManager.find(Customer.class, 1);
        System.out.println(customer);
        customer.setFirstName("Jane");
        Customer updatedCustomer = customerRepository.save(customer);
        assertThat(updatedCustomer.getFirstName()).isEqualTo("Jane");
    }

    @Test
    public void testGetCustomer() {
        Optional<Customer> customer = customerRepository.findById(1);
        assertThat(customer).isPresent();
        assertThat(customer.get().getId()).isGreaterThan(0);
    }

    @Test
    public void testDeleteCustomer() {
        Integer id  = 2;
        customerRepository.deleteById(id);
        Optional<Customer> customer = customerRepository.findById(id);
        assertThat(customer).isNotPresent();
    }

    @Test
    public void testFindByEmail() {
        String email = "john.doe@example.com";
       Customer customer =  customerRepository.findByEmail(email);
       assertThat(customer).isNotNull();
    }

    @Test
    public void testFindByVerificationCode() {
        String verificationCode = "verifycode123john";
        Customer customer = customerRepository.findByVerificationCode(verificationCode);
        assertThat(customer).isNotNull();
    }

    @Test
    public void testEnabledCustomer(){
        Customer customer = entityManager.find(Customer.class, 1);
        customer.setEnabled(true);
        Customer updatedCustomer = customerRepository.save(customer);
        assertThat(updatedCustomer.isEnabled()).isTrue();
    }

    @Test
    public void testUpdateAuthenticationType() {
            Integer id = 1;
            customerRepository.updateAuthenticationType(id, AuthenticationType.DATABASE);

            Customer customer = customerRepository.findById(id).get();
            assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.DATABASE);
    }
}
