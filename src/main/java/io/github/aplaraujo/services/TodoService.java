package io.github.aplaraujo.services;

import io.github.aplaraujo.dto.TodoDTO;
import io.github.aplaraujo.entities.Todo;
import io.github.aplaraujo.entities.User;
import io.github.aplaraujo.mappers.TodoMapper;
import io.github.aplaraujo.repositories.TodoRepository;
import io.github.aplaraujo.repositories.UserRepository;
import io.github.aplaraujo.services.exceptions.OperationNotAllowedException;
import io.github.aplaraujo.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;
    private final UserRepository userRepository;

    public TodoDTO insert(TodoDTO dto) {
        Todo todo = todoMapper.toEntity(dto);
        User user = userRepository.getReferenceById(dto.userId());
        todo.setUser(user);
        todo = todoRepository.save(todo);
        return todoMapper.toDTO(todo);
    }

    public List<Todo> getAllTodosByUser(Long userId) {
        if (userId != null) {
            return todoRepository.findByUserId(userId);
        }
        return todoRepository.findAll();
    }

    public Todo getTodoByIdAndUser(Long id, Long userId) {
        if (id == null || userId == null) {
            throw new IllegalArgumentException("Todo ID and User ID must not be null");
        }
        return todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> {
                    if (todoRepository.existsById(id)) {
                        throw new OperationNotAllowedException("You don't have permission to access this todo");
                    } else {
                        throw new ResourceNotFoundException("Todo not found with id: " + id);
                    }
                });
    }

    public Todo updateTodo(Long id, Long userId, TodoDTO dto) {
        Todo todo = getTodoByIdAndUser(id, userId);
        todoMapper.updateEntityFromDTO(todo, dto);
        return todoRepository.save(todo);
    }

    public Todo patchTodo(Long id, Long userId, TodoDTO dto) {
        Todo todo = getTodoByIdAndUser(id, userId);
        todoMapper.patchEntityFromDTO(todo, dto);
        return todoRepository.save(todo);
    }

    public void delete(Long id, Long userId) {
        Todo todo = getTodoByIdAndUser(id, userId);
        if (todoRepository.existsById(id)) {
            throw new OperationNotAllowedException("You don't have permission to access this todo");
        } else {
            throw new ResourceNotFoundException("Todo not found with id: " + id);
        }

    }
}
