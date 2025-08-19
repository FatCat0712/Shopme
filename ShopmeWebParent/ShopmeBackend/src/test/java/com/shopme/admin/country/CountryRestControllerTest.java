package com.shopme.admin.country;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.country.Country;
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
public class CountryRestControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CountryRepository countryRepository;

    @Autowired
    public CountryRestControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, CountryRepository countryRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.countryRepository = countryRepository;
    }

    @Test
    @WithMockUser(username = "name@codejava.net", password = "nam2020", authorities = "Admin")
    public void testListCountries() throws Exception {
        String url = "/countries/list";
        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

//        convert object to json
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println(jsonResponse);

//        convert json to list of objects
        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);

        for(Country country : countries) {
            System.out.println(country);
        }

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "something", roles = "ADMIN")
    public void testCreateCountry() throws Exception {
        String url = "/countries/save";
        String countryName = "Germany";
        String countryCode = "DE";
        Country country = new Country(countryName, countryCode);

        MvcResult mvcResult = mockMvc.perform(
                post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        Integer countryId = Integer.parseInt(response);
        Optional<Country> findById = countryRepository.findById(countryId);

        assertThat(findById.isPresent()).isTrue();

       Country savedCountry = findById.get();

       assertThat(savedCountry.getName()).isEqualTo(countryName);

        System.out.println("Country ID: " +response);


    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "something", roles = "ADMIN")
    public void testUpdateCountry() throws Exception {
        String url = "/countries/save";
        Integer countryId = 7;
        String countryName = "Singapore";
        String countryCode = "SNG";
        Country country = new Country(countryId, countryName, countryCode);

            mockMvc.perform(
                        post(url).contentType("application/json")
                                .content(objectMapper.writeValueAsString(country))
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(countryId)));

        Optional<Country> findById = countryRepository.findById(countryId);

        assertThat(findById.isPresent()).isTrue();

        Country savedCountry = findById.get();

        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username = "nam@codejava.net", password = "something", roles = "ADMIN")
    public void testDeleteCountry() throws Exception {
        Integer countryId = 3;
        String url = "/countries/delete/" + countryId;

        mockMvc.perform(
                            get(url).with(csrf())
                )
                .andExpect(status().isOk());
        Optional<Country> findById = countryRepository.findById(countryId);

        assertThat(findById).isNotPresent();
    }


}