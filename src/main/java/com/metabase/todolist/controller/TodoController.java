package com.metabase.todolist.controller;

import com.metabase.todolist.model.Todo;
import com.metabase.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    @Autowired
    private TodoService todoService;

    @PostMapping("/create")
    public ResponseEntity<Todo> createTodo(
            @RequestParam String title,
            @RequestParam(required = false) String description) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        Todo createdTodo = todoService.createTodo(todo);
        if (createdTodo != null) {
            // 使用 builder 模式返回 201 Created 状态和创建的 Todo 对象
            return ResponseEntity.created(null).body(createdTodo); // Location header is null for simplicity
        } else {
            // 如果创建失败（例如 service 返回 null），返回 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Todo todo = todoService.getTodoById(id);
        if (todo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(todo, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        List<Todo> todos = todoService.getAllTodos();
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Todo> updateTodo(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean completed) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setCompleted(completed != null ? completed : false);
        Todo updatedTodo = todoService.updateTodo(id, todo);
        if (updatedTodo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteTodo(@RequestParam Long id) {
        todoService.deleteTodo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}