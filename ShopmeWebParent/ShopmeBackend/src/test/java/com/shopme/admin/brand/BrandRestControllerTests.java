package com.shopme.admin.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.brand.BrandDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BrandRestControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public BrandRestControllerTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @WithMockUser(username = "name@codejava.net", password = "nam2020", authorities = "Admin")
    public void testListBrands() throws Exception {
        String url = "/brands/list_brands";
        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

//        convert object to json
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println(jsonResponse);

//        convert json to list of objects
       BrandDTO[] brands = objectMapper.readValue(jsonResponse,BrandDTO[].class);

        for(BrandDTO brand : brands) {
            System.out.println(brand);
        }

        assertThat(brands).hasSizeGreaterThan(0);
    }
}
