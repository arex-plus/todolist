package com.metabase.todolist.service.impl;

import com.metabase.todolist.dao.TodoSQLiteDAO;
import com.metabase.todolist.model.Todo;
import com.metabase.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {
    @Autowired
    private TodoSQLiteDAO todoDAO;

    @Override
    public Todo createTodo(Todo todo) {
        return todoDAO.create(todo);
    }

    @Override
    public Todo getTodoById(Long id) {
        return todoDAO.findById(id);
    }

    @Override
    public List<Todo> getAllTodos() {
        return todoDAO.findAll();
    }

    @Override
    public Todo updateTodo(Long id, Todo todo) {
        return todoDAO.update(id, todo);
    }

    @Override
    public void deleteTodo(Long id) {
        todoDAO.delete(id);
    }
}