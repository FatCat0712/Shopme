package com.shopme.admin.country;

import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CountryRepositoryTest {
    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testCreateCountry() {
        Country canada = new Country("Canada", "+1");
        Country china = new Country("China", "+86");
        Country russia = new Country("Russia", "+7");
        Country japan = new Country("Japan", "+81");
        Country vietnam = new Country("Vietnam", "+84");
        Iterable<Country> savedCountries = countryRepository.saveAll(List.of(canada, china, russia, japan, vietnam));
        assertThat(savedCountries).size().isGreaterThan(0);
    }

    @Test
    public void testListCountries() {
        List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
        listCountries.forEach(System.out::println);
        assertThat(listCountries.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCountry() {
        Country country = countryRepository.findById(3).get();
        country.setCode("+44");
        Country savedCountry = countryRepository.save(country);
        assertThat(savedCountry.getCode()).isEqualTo("+44");
    }

    @Test
    public void testGetCountry() {
        Country country = countryRepository.findById(1).get();
        assertThat(country.getId()).isGreaterThan(0);
    }

    @Test
    public void testDeleteCountry() {
        countryRepository.deleteById(2);
        Optional<Country> country = countryRepository.findById(2);
        assertThat(country.isEmpty()).isTrue();
    }

}
