package io.github.aplaraujo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.aplaraujo.dto.TodoDTO;
import io.github.aplaraujo.entities.enums.PriorityType;
import io.github.aplaraujo.tests.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "JWT_SECRET=oJPnpbp72Q7EGIVpRoZFneysc0rJHRAJYwy6HEpTJZ0",
        "JWT_DURATION=86400"
})
@AutoConfigureMockMvc
@Transactional
public class TodoControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;

    private String existentEmail, existentPassword, token;
    private Long existentTodoId, nonExistentTodoId;

    @BeforeEach
    void setUp() throws Exception {
        existentEmail = "carolinenairdacosta@outllok.com";
        existentPassword = "qo7w7BN7wX";
        existentTodoId = 1L;
        nonExistentTodoId = 100L;
        token = tokenUtil.obtainAccessTokenForTest(mockMvc, existentEmail, existentPassword);
    }

    @Test
    public void findTodosShouldReturn200WhenUserIsLogged() throws Exception {
        ResultActions result = mockMvc.perform(get("/todos").header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
    }

    @Test
    public void findTodosShouldReturn401WhenUserIsNotLogged() throws Exception {
        ResultActions result = mockMvc.perform(get("/todos").accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void findTodoByIdShouldReturn200WhenUserIsLogged() throws Exception {
        ResultActions result = mockMvc.perform(get("/todos/" + existentTodoId).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON));
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
        ResultActions result = mockMvc.perform(get("/todos/" + todoId).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isForbidden());
    }

    @Test
    public void findTodoByIdShouldReturn404WhenUserIsLoggedAndIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(get("/todos/" + nonExistentTodoId).header("Authorization", "Bearer " + token).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void insertTodoShouldReturn201WhenUserIsLogged() throws Exception {
         TodoDTO dto = new TodoDTO(8L, "Comprar leite", "Lorem ipsum dolor sit amet", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated());
    }

    @Test
    public void insertTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndNameFieldIsEmpty() throws Exception {
         TodoDTO dto = new TodoDTO(8L, "  ", "Lorem ipsum dolor sit amet", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.status").value(422));
        result.andExpect(jsonPath("$.message").value("Invalid data"));
        result.andExpect(jsonPath("$.errors[0].field").value("name"));
        result.andExpect(jsonPath("$.errors[0].error").value("This field should not be empty"));
    }

    @Test
    public void insertTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndDescriptionFieldIsEmpty() throws Exception {
         TodoDTO dto = new TodoDTO(8L, "Comprar leite", "  ", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.status").value(422));
        result.andExpect(jsonPath("$.message").value("Invalid data"));
        result.andExpect(jsonPath("$.errors[0].field").value("description"));
        result.andExpect(jsonPath("$.errors[0].error").value("This field should not be empty"));
    }

    @Test
    public void insertTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndDoneFieldIsNull() throws Exception {
        TodoDTO dto = new TodoDTO(8L, "Comprar leite", "Lorem ipsum dolor sit amet", null, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.status").value(422));
        result.andExpect(jsonPath("$.message").value("Invalid data"));
        result.andExpect(jsonPath("$.errors[0].field").value("done"));
        result.andExpect(jsonPath("$.errors[0].error").value("This field should not be null"));
    }

    @Test
    public void insertTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndPriorityFieldIsNull() throws Exception {
         TodoDTO dto = new TodoDTO(8L, "Comprar leite", "Lorem ipsum dolor sit amet", true, null, 1L);
        ResultActions result = mockMvc.perform(post("/todos").header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.status").value(422));
        result.andExpect(jsonPath("$.message").value("Invalid data"));
        result.andExpect(jsonPath("$.errors[0].field").value("priority"));
        result.andExpect(jsonPath("$.errors[0].error").value("This field should not be null"));
    }

    @Test
    public void insertTodoShouldReturn401WhenUserIsNotLogged() throws Exception {
        TodoDTO dto = new TodoDTO(8L, "Comprar leite", "Lorem ipsum dolor sit amet", true, PriorityType.MEDIA, 1L);
        ResultActions result = mockMvc.perform(post("/todos")
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void updateTodoShouldReturn200WhenUserIsLogged() throws Exception {
        TodoDTO dto = new TodoDTO(existentTodoId, "Lavar a louça", "Descrição atualizada", true, PriorityType.BAIXA, 1L);
        ResultActions result = mockMvc.perform(put("/todos/" + existentTodoId).header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existentTodoId));
        result.andExpect(jsonPath("$.name").value("Lavar a louça"));
        result.andExpect(jsonPath("$.description").value("Descrição atualizada"));
        result.andExpect(jsonPath("$.done").value(true));
        result.andExpect(jsonPath("$.priority").value("BAIXA"));
    }

    @Test
    public void updateTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndNameFieldNameIsEmpty() throws Exception {
        TodoDTO dto = new TodoDTO(existentTodoId, "    ", "Descrição atualizada", true, PriorityType.BAIXA, 1L);
        ResultActions result = mockMvc.perform(put("/todos/" + existentTodoId).header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.status").value(422));
        result.andExpect(jsonPath("$.message").value("Invalid data"));
        result.andExpect(jsonPath("$.errors[0].field").value("name"));
        result.andExpect(jsonPath("$.errors[0].error").value("This field should not be empty"));
    }

    @Test
    public void updateTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndDescriptionFieldIsEmpty() throws Exception {
        TodoDTO dto = new TodoDTO(existentTodoId, "Comprar leite", "    ", true, PriorityType.BAIXA, 1L);
        ResultActions result = mockMvc.perform(put("/todos/" + existentTodoId).header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.status").value(422));
        result.andExpect(jsonPath("$.message").value("Invalid data"));
        result.andExpect(jsonPath("$.errors[0].field").value("description"));
        result.andExpect(jsonPath("$.errors[0].error").value("This field should not be empty"));
    }

    @Test
    public void updateTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndDoneFieldIsNull() throws Exception {
        TodoDTO dto = new TodoDTO(existentTodoId, "Lavar a louça", "Descrição atualizada", null, PriorityType.BAIXA, 1L);
        ResultActions result = mockMvc.perform(put("/todos/" + existentTodoId).header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.status").value(422));
        result.andExpect(jsonPath("$.message").value("Invalid data"));
        result.andExpect(jsonPath("$.errors[0].field").value("done"));
        result.andExpect(jsonPath("$.errors[0].error").value("This field should not be null"));
    }

    @Test
    public void updateTodoShouldReturn422AndCustomMessageWhenUserIsLoggedAndPriorityFieldIsNull() throws Exception {
        TodoDTO dto = new TodoDTO(existentTodoId, "Lavar a louça", "Descrição atualizada", true, null, 1L);
        ResultActions result = mockMvc.perform(put("/todos/" + existentTodoId).header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnprocessableEntity());
        result.andExpect(jsonPath("$.status").value(422));
        result.andExpect(jsonPath("$.message").value("Invalid data"));
        result.andExpect(jsonPath("$.errors[0].field").value("priority"));
        result.andExpect(jsonPath("$.errors[0].error").value("This field should not be null"));
    }

    @Test
    public void updateTodoShouldReturn401WhenUserIsNotLogged() throws Exception {
        TodoDTO dto = new TodoDTO(existentTodoId, "Lavar a louça", "Descrição atualizada", true, PriorityType.BAIXA, 1L);
        ResultActions result = mockMvc.perform(put("/todos/" + existentTodoId)
                .content(objectMapper.writeValueAsString(dto)).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteTodoShouldReturn204WhenUserIsLoggedAndIdExists() throws Exception {
        ResultActions result = mockMvc.perform(delete("/todos/" + existentTodoId).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteTodoShouldReturn404WhenUserIsLoggedAndIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(delete("/todos/" + nonExistentTodoId).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteTodoShouldReturn401WhenUserIsNotLogged() throws Exception {
        ResultActions result = mockMvc.perform(delete("/todos/" + existentTodoId).contentType(MediaType.APPLICATION_JSON));
        result.andExpect(status().isUnauthorized());
    }

}
