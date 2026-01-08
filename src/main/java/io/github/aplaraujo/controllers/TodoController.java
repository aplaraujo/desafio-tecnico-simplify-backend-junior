package io.github.aplaraujo.controllers;

import io.github.aplaraujo.dto.TodoDTO;
import io.github.aplaraujo.entities.Todo;
import io.github.aplaraujo.mappers.TodoMapper;
import io.github.aplaraujo.security.UserDetailsImpl;
import io.github.aplaraujo.services.TodoService;
import io.github.aplaraujo.services.exceptions.OperationNotAllowedException;
import io.github.aplaraujo.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/todos")
@RequiredArgsConstructor
public class TodoController implements GenericController{
    private final TodoService todoService;
    private final TodoMapper todoMapper;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<Todo>> findTodos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        List<Todo> todos = todoService.getAllTodosByUser(userId);
        return ResponseEntity.ok(todos);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<TodoDTO> findTodoById(@PathVariable("id") String id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        var todoId = Long.parseLong(id);
        Todo todo = todoService.getTodoByIdAndUser(todoId, userId);
        return ResponseEntity.ok(todoMapper.toDTO(todo));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<Void> insertTodo(@RequestBody @Valid TodoDTO dto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.insert(dto);
        var url = generateHeaderLocation(dto.id());
        return ResponseEntity.created(url).build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable("id") String id,
            @Valid @RequestBody TodoDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        var todoId = Long.parseLong(id);
        Long userId = userDetails.getId();
        Todo updated = todoService.updateTodo(todoId, userId, dto);
        return ResponseEntity.ok(todoMapper.toDTO(updated));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            var todoId = Long.parseLong(id);
            Long userId = userDetails.getId();
            todoService.delete(todoId, userId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (OperationNotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
