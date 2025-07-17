package com.shopme.admin.currency;

import com.shopme.common.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CurrencyRepositoryTest {
    @Autowired
    private CurrencyRepository currencyRepository;


    @Test
    public void testCreateCurrencies() {
        Currency aud = new Currency("Australian Dollar", "$", "AUD");
        Currency brl = new Currency("Brazilian real", "R$", "BRL");
        Currency gbp = new Currency("British Pound", "£", "GPB");
        Currency cad = new Currency("Canadian Dollar", "$", "CAD");
        Currency cny = new Currency("Chinese Yuan", "¥", "CNY");
        Currency eur = new Currency("Euro", "€", "EUR");
        Currency inr = new Currency("Indian Rupee", "₹", "INR");
        Currency jpy = new Currency("Japanese Yen", " ¥", "JPY");
        Currency rub = new Currency("Russian Ruble", "₽", "RUB");
        Currency krw = new Currency("South Korean Won", "₩", "KRW");
        Currency usd = new Currency("United States Dollar", "$", "USD");
        Currency vnd = new Currency("Vietnamese Dong", "đ", "VND");

        Iterable<Currency> savedCurrencies = currencyRepository.saveAll(List.of(aud, brl, gbp, cad, cny, eur, inr, jpy, rub, krw, usd, vnd));

        assertThat(savedCurrencies).size().isGreaterThan(0);

    }

    @Test
    public void testListAllOrderByNameAsc() {
        List<Currency> currencies = currencyRepository.findAllByOrderByNameAsc();

        currencies.forEach(System.out::println);

        assertThat(currencies).size().isGreaterThan(0);
    }
}
