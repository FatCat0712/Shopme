package com.shopme.address;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.setting.CountryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AddressService {
    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository, CountryRepository countryRepository) {
        this.addressRepository = addressRepository;
        this.countryRepository = countryRepository;
    }

    public List<Address> listAddressBook(Customer customer) {
            return addressRepository.findByCustomer(customer);
    }

    public List<Country> listCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(Address address) {
        addressRepository.save(address);
    }

    public Address get(Integer id, Integer customerId) {
       return  addressRepository.findByIdAndCustomer(id, customerId);
    }

    public void delete(Integer id, Integer customerId)  {
        addressRepository.deleteByIdAndCustomer(id, customerId);
    }

    public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
        if (defaultAddressId > 0) {
            addressRepository.setDefaultAddress(defaultAddressId);
        }
        addressRepository.setNonDefaultForOther(defaultAddressId, customerId);
    }

    public Address getDefaultAddress(Customer customer) {
        return addressRepository.findByDefaultCustomer(customer.getId());
    }



}
