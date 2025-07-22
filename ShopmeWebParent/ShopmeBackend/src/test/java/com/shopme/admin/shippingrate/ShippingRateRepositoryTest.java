package com.shopme.admin.shippingrate;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ShippingRateRepositoryTest {
    private final ShippingRateRepository shippingRateRepository;

    @Autowired
    public ShippingRateRepositoryTest(ShippingRateRepository shippingRateRepository) {
        this.shippingRateRepository = shippingRateRepository;
    }

    @Test
    public void testCreateNew() {
        Country country = new Country(1);
        ShippingRate shippingRate = new ShippingRate();
        shippingRate.setRate(1.23f);
        shippingRate.setDays(3);
        shippingRate.setCodSupported(false);
        shippingRate.setCountry(country);
        shippingRate.setState("California");

        ShippingRate saveShippingRate = shippingRateRepository.save(shippingRate);

        assertThat(saveShippingRate.getId()).isGreaterThan(0);

    }

    @Test
    public void testUpdate() {
        Float rate = 1.75f;
        Integer days = 5;
        Boolean codSupported = true;

        Optional<ShippingRate> shippingRateById = shippingRateRepository.findById(1);
        assertTrue(shippingRateById.isPresent());

        ShippingRate savedShippingRate = shippingRateById.get();

        savedShippingRate.setRate(rate);
        savedShippingRate.setDays(days);
        savedShippingRate.setCodSupported(codSupported);

        ShippingRate savedData = shippingRateRepository.save(savedShippingRate);

        assertThat(savedData.getRate()).isEqualTo(rate);
        assertThat(savedData.getDays()).isEqualTo(days);
        assertThat(savedData.getCodSupported()).isEqualTo(codSupported);

    }

    @Test
    public void testDelete() {
        shippingRateRepository.deleteById(1);
        Optional<ShippingRate> shippingRate = shippingRateRepository.findById(1);

        assertThat(shippingRate).isEmpty();
    }

    @Test
    public void testFindAll() {
        int pageNum = 0;
        Pageable pageable = PageRequest.of(pageNum, 10);
        Page<ShippingRate> listShippingRatesByPage = shippingRateRepository.findAll("Da Nang", pageable);

        List<ShippingRate> listShippingRates = listShippingRatesByPage.getContent();

        assertThat(listShippingRates.size()).isGreaterThan(0);

        listShippingRates.forEach(System.out::println);
    }

    @Test
    public void testFindByCountryAndState() {
        ShippingRate shippingRate = shippingRateRepository.findByCountryAndState(234, "California");
        assertThat(shippingRate.getId()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCODSupport() {
        Integer id = 3;
        shippingRateRepository.updateCODSupported(id, true);
        Optional<ShippingRate> shippingRate = shippingRateRepository.findById(id);
        assertTrue(shippingRate.isPresent());
        ShippingRate updatedShippingRate = shippingRate.get();
        assertThat(updatedShippingRate.getCodSupported()).isTrue();
    }



}

