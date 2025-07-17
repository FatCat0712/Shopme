package com.shopme.setting;

import com.shopme.common.entity.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends CrudRepository<Country, Integer> {
    List<Country> findAllByOrderByNameAsc();
    Country findByCode(String code);
}
