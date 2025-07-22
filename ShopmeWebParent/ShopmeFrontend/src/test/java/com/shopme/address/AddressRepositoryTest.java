package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class AddressRepositoryTest {
    private final AddressRepository addressRepository;

    @Autowired
    public AddressRepositoryTest(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Test
    public void testAddNewAddress() {
        Customer customer = new Customer(1);
        Country country = new Country(234);

        Address address = new Address();
        address.setFirstName("Tobie");
        address.setLastName("Abel");
        address.setPhoneNumber("19094644165");
        address.setAddressLine1("222 Doi Cung");
        address.setAddressLine2("District 11");
        address.setCity("Ho Chi Minh City");
        address.setState("");
        address.setPostalCode("650000");
        address.setCustomer(customer);
        address.setCountry(country);
        address.setDefaultForShipping(true);

        Address newAddress = addressRepository.save(address);

        assertThat(newAddress.getId()).isGreaterThan(0);

    }

    @Test
    public void testFindByCustomer() {
        Customer customer = new Customer(1);
        List<Address> savedAddresses = addressRepository.findByCustomer(customer);
        assertThat(savedAddresses.size()).isGreaterThan(0);
        savedAddresses.forEach(System.out::println);
    }

    @Test
    public void testFindByIdAndCustomer() {
        Integer addressId = 1;
        Integer customerId = 1;
        Address address = addressRepository.findByIdAndCustomer(addressId, customerId);
        assertThat(address.getId()).isGreaterThan(0);
        System.out.println(address);
    }

    @Test
    public void testUpdate() {
        Optional<Address> address = addressRepository.findById(3);
        assertTrue(address.isPresent());
        Address savedAddress = address.get();
        savedAddress.setFirstName("Mark");
        savedAddress.setLastName("Zuckerberg");
        Address updatedAddress = addressRepository.save(savedAddress);
        assertThat(updatedAddress.getFirstName() + " " + updatedAddress.getLastName()).isEqualTo("Mark Zuckerberg");
    }

    @Test
    public void testDeleteByIdAndCustomer() {
        Integer addressId =1;
        Integer customerId = 1;
        addressRepository.deleteByIdAndCustomer(addressId, customerId);
        Optional<Address> deletedAddress = addressRepository.findById(addressId);
        assertThat(deletedAddress).isNotPresent();
    }

    @Test
    public void testSetDefault() {
        Integer addressId = 7;
        addressRepository.setDefaultAddress(addressId);
        Optional<Address> address = addressRepository.findById(addressId);
        assertTrue(address.isPresent());
        Address savedAddress = address.get();
        assertThat(savedAddress.isDefaultForShipping()).isTrue();
    }

    @Test
    public void testSetNonDefaultAddresses() {
        Integer addressId = 7;
        Integer customerId = 1;
        addressRepository.setNonDefaultForOther(addressId, customerId);
    }
}
