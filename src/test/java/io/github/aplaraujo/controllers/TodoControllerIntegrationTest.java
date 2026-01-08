package io.github.aplaraujo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.aplaraujo.dto.TodoDTO;
import io.github.aplaraujo.entities.enums.PriorityType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    public void insertTodoShouldReturn201WhenUserIsLogged() throws Exception {
         TodoDTO dto = new TodoDTO(8L, "Comprar leite", "Lorem ipsum dolor sit amet", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", this.token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated());
    }

    @Test
    public void insertTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndNameFieldIsEmpty() throws Exception {
         TodoDTO dto = new TodoDTO(8L, "  ", "Lorem ipsum dolor sit amet", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", this.token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(res -> assertEquals("This field should not be empty", res.getResolvedException().getMessage()));
    }

    @Test
    public void insertTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndDescriptionFieldIsEmpty() throws Exception {
         TodoDTO dto = new TodoDTO(8L, "Comprar leite", "  ", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", this.token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(res -> assertEquals("This field should not be empty", res.getResolvedException().getMessage()));
    }

//    @Test
//    public void insertTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndPriorityFieldIsEmpty() throws Exception {
//         TodoDTO dto = new TodoDTO(4L, "Comprar leite", "Lorem ipsum dolor sit amet", true, PriorityType.MEDIA, 1L);
//        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", this.token)
//                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
//        result.andExpect(status().isUnprocessableEntity());
//        result.andExpect(res -> assertEquals("This field should not be empty", res.getResolvedException().getMessage()));
//    }

    @Test
    public void insertTodoShouldReturn401WhenUserIsNotLogged() throws Exception {
        TodoDTO dto = new TodoDTO(8L, "Comprar leite", "Lorem ipsum dolor sit amet", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos")
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateTodoShouldReturn204WhenUserIsLogged() throws Exception {
        Long todoId = 8L;
         TodoDTO dto = new TodoDTO(todoId, "Comprar leite", "Descrição atualizada", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", this.token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
    }

    @Test
    public void updateTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndNameFieldIsEmpty() throws Exception {
         Long todoId = 8L;
         TodoDTO dto = new TodoDTO(todoId, "  ", "Descrição atualizada", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", this.token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(res -> assertEquals("This field should not be empty", res.getResolvedException().getMessage()));
    }

    @Test
    public void updateTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndDescriptionFieldIsEmpty() throws Exception {
         Long todoId = 8L;
         TodoDTO dto = new TodoDTO(todoId, "Comprar leite", "  ", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", this.token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(res -> assertEquals("This field should not be empty", res.getResolvedException().getMessage()));
    }

//    @Test
//    public void updateTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndPriorityFieldIsEmpty() throws Exception {
//         Long todoId = 4L;
//         TodoDTO dto = new TodoDTO(todoId, "Comprar leite", "Lorem ipsum dolor sit amet", true, "", 1L);
//        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", this.token)
//                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
//        result.andExpect(status().isUnprocessableEntity());
//        result.andExpect(res -> assertEquals("This field should not be empty", res.getResolvedException().getMessage()));
//    }

    @Test
    public void updateTodoShouldReturn401WhenUserIsNotLogged() throws Exception {
         Long todoId = 8L;
         TodoDTO dto = new TodoDTO(todoId, "Comprar leite", "Descrição atualizada", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos")
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteTodoShouldReturn204WhenUserIsLoggedAndIdExists() throws Exception {
        Long todoId = 4L;
        ResultActions result = mockMvc.perform(delete("/todos/" + todoId).header("Authorization", this.token).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteTodoShouldReturn404WhenUserIsLoggedAndIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(delete("/todos/" + nonExistentTodoId).header("Authorization", this.token).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteTodoShouldReturn401WhenUserIsNotLogged() throws Exception {
        ResultActions result = mockMvc.perform(delete("/todos/" + nonExistentTodoId).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }

}
