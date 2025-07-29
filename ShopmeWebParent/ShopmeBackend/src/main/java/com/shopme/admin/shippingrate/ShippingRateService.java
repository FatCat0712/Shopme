package com.shopme.admin.shippingrate;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShippingRateService {
    public final int SHIPPING_RATE_PER_PAGE = 10;
    private final ShippingRateRepository shippingRateRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public ShippingRateService(ShippingRateRepository shippingRateRepository, CountryRepository countryRepository) {
        this.shippingRateRepository = shippingRateRepository;
        this.countryRepository = countryRepository;
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, SHIPPING_RATE_PER_PAGE, shippingRateRepository);
    }

    public List<Country> listCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(ShippingRate shippingRate) {
        shippingRateRepository.save(shippingRate);
    }

    public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
        Optional<ShippingRate> shippingRate = shippingRateRepository.findById(id);
        if(shippingRate.isEmpty()) {
            throw new ShippingRateNotFoundException("Could not find any shipping rate with id " + id);
        }
        else {
            return shippingRate.get();
        }
    }

    public void delete(Integer id) throws ShippingRateNotFoundException {
        Long isExist = shippingRateRepository.countById(id);
        if(isExist == 0) {
            throw new ShippingRateNotFoundException("Could not find any shipping rate with id " + id);
        }
        shippingRateRepository.deleteById(id);

    }

    public void updateCODSupport(Integer id, Boolean status) throws ShippingRateNotFoundException {
        Optional<ShippingRate> shippingRate = shippingRateRepository.findById(id);
        if(shippingRate.isEmpty()) {
            throw new ShippingRateNotFoundException("Could not find any shipping rate with id " + id);
        }
        else {
            ShippingRate savedShippingRate = shippingRate.get();
            savedShippingRate.setCodSupported(status);
            shippingRateRepository.save(savedShippingRate);
        }
    }

    public void checkUnique(Integer id, Country country, String state) throws ShippingRateAlreadyExistsException {
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(country.getId(), state);
        if(shippingRate != null && !shippingRate.getId().equals(id)) {
            throw new ShippingRateAlreadyExistsException(String.format("There's already a rate for destination %s, %s", country.getName(), state));
        }
    }



}
