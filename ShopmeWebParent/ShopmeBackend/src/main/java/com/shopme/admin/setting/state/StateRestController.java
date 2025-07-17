package com.shopme.admin.setting.state;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import com.shopme.common.entity.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StateRestController {
    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/states/list_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer id) {
            List<State> statesByCountry = stateRepository.findByCountryOrderByNameAsc(new Country(id));
            return statesByCountry.stream().map(state -> new StateDTO(state.getId(), state.getName())).toList();
    }

    @PostMapping("/states/save")
    public String save(@RequestBody State state) {
        State savedState = stateRepository.save(state);
        return String.valueOf(savedState.getId());
    }

    @DeleteMapping("/states/delete/{id}")
    public void deleteById(@PathVariable("id") Integer id) {
        stateRepository.deleteById(id);
    }
}
