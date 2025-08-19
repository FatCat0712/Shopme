package com.shopme.admin.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.article.ArticleDTO;
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
public class ArticleRestControllerTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public ArticleRestControllerTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @WithMockUser(username = "name@codejava.net", password = "nam2020", authorities = "Admin")
    public void testListArticles() throws Exception {
        String url = "/articles/list_articles";
        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

//        convert object to json
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println(jsonResponse);

//        convert json to list of objects
        ArticleDTO[] articles = objectMapper.readValue(jsonResponse, ArticleDTO[].class);

        for(ArticleDTO article : articles) {
            System.out.println(article.id() + " " + article.title());
        }

        assertThat(articles).hasSizeGreaterThan(0);
    }
}
