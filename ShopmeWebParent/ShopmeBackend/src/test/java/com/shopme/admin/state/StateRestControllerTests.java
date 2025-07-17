package com.shopme.admin.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.StateDTO;
import com.shopme.admin.setting.state.StateRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StateRepository stateRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @WithMockUser(username = "name@codejava.net", password = "java2020", roles = "ADMIN")
    public void testListByCountries() throws Exception {
        String url = "/states/list";

        Country country = entityManager.find(Country.class, 1);

        MvcResult result = mockMvc
                .perform(
                        get(url).contentType("application/json")
                                .content(objectMapper.writeValueAsString(country))
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        System.out.println(jsonResponse);

        StateDTO[] states = objectMapper.readValue(jsonResponse, StateDTO[].class);

        for (StateDTO state: states){
            System.out.println(state);
        }

        assertThat(states).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "name@codejava.net", password = "java2020", roles = "ADMIN")
    public void testCreateState() throws Exception {
        String url = "/states/save";
        Country country = entityManager.find(Country.class, 6);
        String stateName = "Hanoi";
        State state = new State(stateName, country);

        MvcResult result = mockMvc
                .perform(
                            post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(state))
                        .with(csrf())
                ).andDo(print())
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        Integer stateId = Integer.parseInt(jsonResponse);

         Optional<State> stateById = stateRepository.findById(stateId);

         assertThat(stateById).isPresent();

         State savedState = stateById.get();

         assertThat(savedState.getName()).isEqualTo(stateName);

        System.out.println("State ID: " + jsonResponse);

    }

    @Test
    @WithMockUser(username = "name@codejava.net", password = "java2020", roles = "ADMIN")
    public void testUpdateState() throws Exception {
        String url = "/states/save";
        Integer stateId = 5;
        Country country = entityManager.find(Country.class, 6);
        String stateName = "Ho Chi Minh";
        State state = new State(stateId, stateName, country);

             mockMvc
                .perform(
                        post(url)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(state))
                                .with(csrf())
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(stateId)));


        Optional<State> stateById = stateRepository.findById(stateId);

        assertThat(stateById).isPresent();

        State savedState = stateById.get();

        assertThat(savedState.getName()).isEqualTo(stateName);

    }


    @Test
    @WithMockUser(username = "name@codejava.net", password = "java2020", roles = "ADMIN")
    public void testDeleteState() throws Exception {
        Integer stateId = 5;
        String url = "/states/delete/" + stateId;

        mockMvc
                .perform(
                        get(url).with(csrf())
                ).andExpect(status().isOk());


        Optional<State> stateById = stateRepository.findById(stateId);

        assertThat(stateById).isNotPresent();

    }
}
