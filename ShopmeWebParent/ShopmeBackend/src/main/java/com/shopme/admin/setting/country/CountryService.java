package com.shopme.admin.setting.country;

import com.shopme.common.entity.country.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    private final CountryRepository repo;

    @Autowired
    public CountryService(CountryRepository repo) {
        this.repo = repo;
    }

    public List<Country> listAll() {
        return repo.findAll();
    }
}
