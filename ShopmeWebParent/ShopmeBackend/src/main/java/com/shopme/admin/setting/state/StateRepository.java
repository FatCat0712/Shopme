package com.shopme.admin.setting.state;

import com.shopme.common.entity.country.Country;
import com.shopme.common.entity.state.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Integer> {
    List<State> findByCountryOrderByNameAsc(Country country);
}
