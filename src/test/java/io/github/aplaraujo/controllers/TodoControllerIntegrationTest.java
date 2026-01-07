package io.github.aplaraujo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.aplaraujo.tests.TokenUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "JWT_SECRET_KEY=vYGxA52xeFZ9bCR5eUiv5BXhyLIcE4WAAkypXdTkEVU",
        "JWT_DURATION=86400"
})
@AutoConfigureMockMvc
@Transactional
public class TodoControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String existentEmail, existentPassword, token;

    @BeforeEach
    void setUp() throws Exception {
        existentEmail = "carolinenairdacosta@outllok.com";
        existentPassword = "qo7w7BN7wX";

        ResultActions result = this.mockMvc.perform(post("/auth/token").with(httpBasic(existentEmail, existentPassword)));
        MvcResult mvcResult = result.andDo(print()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        JSONObject json = new JSONObject(contentAsString);
        this.token = "Bearer " + json.getJSONObject("data").getString("token");
    }

    @Test
    public void findTodosShouldReturn200WhenUserIsLogged() throws Exception {
        ResultActions result = mockMvc.perform(get("/todos").header("Authorization", this.token).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
    }

    @Test
    public void findTodosShouldReturn401WhenUserIsNotLogged() throws Exception {
        ResultActions result = mockMvc.perform(get("/todos").accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }
}
