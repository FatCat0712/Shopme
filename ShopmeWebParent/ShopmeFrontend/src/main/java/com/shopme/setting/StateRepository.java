package com.shopme.setting;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends CrudRepository<State, Integer> {
    List<State> findByCountryOrderByNameAsc(Country country);
}
