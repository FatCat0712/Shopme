package com.shopme.admin.state;

import com.shopme.admin.setting.state.StateRepository;
import com.shopme.common.entity.country.Country;
import com.shopme.common.entity.state.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class StateRepositoryTests {
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void testCreateState() {
        Country country = testEntityManager.find(Country.class, 1);
        State cali = new State("California");
        State ny = new State("New York");
        State ohio = new State("Ohio");
        State texas = new State("Texas");

        cali.setCountry(country);
        ny.setCountry(country);
        ohio.setCountry(country);
        texas.setCountry(country);

        stateRepository.saveAll(List.of(cali, ohio, ny, texas));
    }

    @Test
    public void testListStatesByCountry() {
        Country country = testEntityManager.find(Country.class, 1);
        List<State> statesByCountry = stateRepository.findByCountryOrderByNameAsc(country);
        statesByCountry.forEach(System.out::println);
        assertThat(statesByCountry.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateState() {
         State state = stateRepository.findById(1).get();
        System.out.println(state);
         state.setName("Donbas");
         State savedState = stateRepository.save(state);
         assertThat(savedState.getName()).isEqualTo("Donbas");
    }

    @Test
    public void testGetState() {
        State country = stateRepository.findById(1).get();
        assertThat(country.getId()).isGreaterThan(0);
    }

    @Test
    public void testDeleteState() {
        stateRepository.deleteById(2);
        Optional<State> country = stateRepository.findById(2);
        assertThat(country.isEmpty()).isTrue();
    }
}
