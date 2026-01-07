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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private Long existentTodoId, nonExistentTodoId;

    @BeforeEach
    void setUp() throws Exception {
        existentEmail = "carolinenairdacosta@outllok.com";
        existentPassword = "qo7w7BN7wX";
        existentTodoId = 1L;
        nonExistentTodoId = 100L;

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

    @Test
    public void findTodoByIdShouldReturn200WhenUserIsLogged() throws Exception {
        ResultActions result = mockMvc.perform(get("/todos/" + existentTodoId).header("Authorization", this.token).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existentTodoId));
        result.andExpect(jsonPath("$.name").value("Lavar a louça"));
        result.andExpect(jsonPath("$.description").value("Lorem ipsum dolor sit amet"));
        result.andExpect(jsonPath("$.done").value(true));
        result.andExpect(jsonPath("$.priority").value("BAIXA"));
    }

    @Test
    public void findTodoByIdShouldReturn401WhenUserIsNotLogged() throws Exception {
        ResultActions result = mockMvc.perform(get("/todos/" + existentTodoId).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void findTodoByIdShouldReturn403WhenTaskDoesNotBelongToUser() throws Exception {
        Long todoId = 6L;
        ResultActions result = mockMvc.perform(get("/todos/" + todoId).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isForbidden());
    }

    @Test
    public void findTodoByIdShouldReturn404WhenUserIsLoggedAndIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/todos/" + nonExistentTodoId).header("Authorization", this.token).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

}
