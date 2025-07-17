package com.shopme.admin.currency;

import com.shopme.common.entity.Currency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
        public List<Currency> findAllByOrderByNameAsc();
}
