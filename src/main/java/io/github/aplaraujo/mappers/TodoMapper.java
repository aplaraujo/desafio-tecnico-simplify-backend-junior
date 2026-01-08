package io.github.aplaraujo.mappers;

import io.github.aplaraujo.dto.TodoDTO;
import io.github.aplaraujo.entities.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {
    public Todo toEntity(TodoDTO dto) {
        Todo todo = new Todo();
        todo.setName(dto.name());
        todo.setDescription(dto.description());
        todo.setDone(dto.done());
        todo.setPriority(dto.priority());
        return todo;
    }

    public TodoDTO toDTO(Todo todo) {
        Long userId = todo.getId() != null ? todo.getUser().getId() : null;
        return new TodoDTO(todo.getId(), todo.getName(), todo.getDescription(), todo.getDone(), todo.getPriority(), userId);
    }
}
