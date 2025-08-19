package com.shopme.admin.setting.country;

import com.shopme.common.entity.country.Country;
import com.shopme.common.entity.country.CountryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CountryRestController {
    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/countries/list")
    public List<CountryDTO> listAll() {
        List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
        return listCountries.stream().map(c -> new CountryDTO(c.getId(), c.getName(), c.getCode())).toList();
    }

    @PostMapping("/countries/save")
    public String save(@RequestBody Country country) {
        Country saveCountry = countryRepository.save(country);
        return String.valueOf(saveCountry.getId());
    }

    @DeleteMapping("/countries/delete/{id}")
    public void delete(@PathVariable(name = "id") Integer id) {
        countryRepository.deleteById(id);
    }


}
