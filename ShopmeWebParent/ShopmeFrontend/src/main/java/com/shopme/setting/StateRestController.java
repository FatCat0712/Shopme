package com.shopme.setting;

import com.shopme.common.entity.country.Country;
import com.shopme.common.entity.state.State;
import com.shopme.common.entity.state.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StateRestController {
    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/settings/list_states_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer id) {
            List<State> statesByCountry = stateRepository.findByCountryOrderByNameAsc(new Country(id));
            return statesByCountry.stream().map(state -> new StateDTO(state.getId(), state.getName())).toList();
    }

}
