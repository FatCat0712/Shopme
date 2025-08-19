package com.shopme.shippingrate;

import com.shopme.common.entity.country.Country;
import com.shopme.common.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShippingRateRepositoryTests {
    private final ShippingRateRepository shippingRateRepository;

    @Autowired
    public ShippingRateRepositoryTests(ShippingRateRepository shippingRateRepository) {
        this.shippingRateRepository = shippingRateRepository;
    }

    @Test
    public void testFindByCountryAndState() {
        Country usa = new Country(234);
        String state = "New York";
        ShippingRate shippingRateByCountryAndState = shippingRateRepository.findByCountryAndState(usa, state);
        assertThat(shippingRateByCountryAndState).isNotNull();
        System.out.println(shippingRateByCountryAndState);
    }
}
