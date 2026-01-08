package io.github.aplaraujo.controllers;

import io.github.aplaraujo.dto.TodoDTO;
import io.github.aplaraujo.entities.Todo;
import io.github.aplaraujo.mappers.TodoMapper;
import io.github.aplaraujo.security.UserDetailsImpl;
import io.github.aplaraujo.services.TodoService;
import lombok.RequiredArgsConstructor;
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
        return todoService.getTodoByIdAndUser(todoId, userId).map(todo -> ResponseEntity.ok(todoMapper.toDTO(todo))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<Void> insertTodo(@RequestBody TodoDTO dto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        todoService.insert(dto);
        var url = generateHeaderLocation(dto.id());
        return ResponseEntity.created(url).build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> update(
            @PathVariable("id") String id,
            @RequestBody TodoDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        var todoId = Long.parseLong(id);
        Long userId = userDetails.getId();
        return todoService.getTodoByIdAndUser(todoId, userId).map(todo -> {
            Todo entity = todoMapper.toEntity(dto);
            todo.setName(entity.getName());
            todo.setDescription(entity.getDescription());
            todo.setDone(entity.getDone());
            todo.setPriority(entity.getPriority());
            todoService.update(todo);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        var todoId = Long.parseLong(id);
        Long userId = userDetails.getId();
        return todoService.getTodoByIdAndUser(todoId, userId).map(todo -> {
            todoService.delete(todoId);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
