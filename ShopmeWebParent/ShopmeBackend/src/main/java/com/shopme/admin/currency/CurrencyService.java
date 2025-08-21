package com.shopme.admin.currency;

import com.shopme.common.entity.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrencyService {
    private final CurrencyRepository repo;

    @Autowired
    public CurrencyService(CurrencyRepository repo) {
        this.repo = repo;
    }

    public Currency get(Integer currencyId) throws CurrencyNotFoundException {
       Optional<Currency> currency = repo.findById(currencyId);
       if(currency.isEmpty()) {
           throw new CurrencyNotFoundException("Could not find any currency with ID " + currencyId);
       }
       return currency.get();
    }
}
