package com.metabase.todolist.service;

import com.metabase.todolist.model.Todo;
import java.util.List;

public interface TodoService {
    Todo createTodo(Todo todo);
    Todo getTodoById(Long id);
    List<Todo> getAllTodos();
    Todo updateTodo(Long id, Todo todo);
    void deleteTodo(Long id);
}