package com.metabase.todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metabase.todolist.model.Todo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.metabase.todolist.dao.TodoSQLiteDAO todoSQLiteDAO;

    private List<Long> createdTodoIds = new ArrayList<>();

    @AfterEach
    public void cleanup() {
        for (Long id : createdTodoIds) {
            todoSQLiteDAO.delete(id);
        }
        createdTodoIds.clear();
    }

    @Test
    public void shouldCreateTodo() throws Exception {
        Todo todo = new Todo("Test Todo", "Test Description");
        var response = mockMvc.perform(post("/api/todos/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Todo"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.completed").value(false))
                .andReturn().getResponse().getContentAsString();
        Todo createdTodo = objectMapper.readValue(response, Todo.class);
        createdTodoIds.add(createdTodo.getId());
    }

    @Test
    public void shouldGetTodoById() throws Exception {
        // First create a todo
        Todo todo = new Todo("Test Todo", "Test Description");
        String response = mockMvc.perform(post("/api/todos/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)))
                .andReturn().getResponse().getContentAsString();
        Todo createdTodo = objectMapper.readValue(response, Todo.class);
        createdTodoIds.add(createdTodo.getId());

        // Then get it by id
        mockMvc.perform(get("/api/todos/" + createdTodo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Todo"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    public void shouldGetAllTodos() throws Exception {
        // Create a todo first
        Todo todo = new Todo("Test Todo", "Test Description");
        String response = mockMvc.perform(post("/api/todos/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)))
                .andReturn().getResponse().getContentAsString();
        Todo createdTodo = objectMapper.readValue(response, Todo.class);
        createdTodoIds.add(createdTodo.getId());

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").exists());
    }

    @Test
    public void shouldUpdateTodo() throws Exception {
        // First create a todo
        Todo todo = new Todo("Test Todo", "Test Description");
        String response = mockMvc.perform(post("/api/todos/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)))
                .andReturn().getResponse().getContentAsString();
        Todo createdTodo = objectMapper.readValue(response, Todo.class);
        createdTodoIds.add(createdTodo.getId());

        // Then update it
        createdTodo.setTitle("Updated Todo");
        createdTodo.setCompleted(true);

        mockMvc.perform(post("/api/todos/update/" + createdTodo.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Todo"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    public void shouldDeleteTodo() throws Exception {
        // First create a todo
        Todo todo = new Todo("Test Todo", "Test Description");
        String response = mockMvc.perform(post("/api/todos/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)))
                .andReturn().getResponse().getContentAsString();
        Todo createdTodo = objectMapper.readValue(response, Todo.class);
        createdTodoIds.add(createdTodo.getId());

        // Then delete it
        mockMvc.perform(post("/api/todos/delete?id=" + createdTodo.getId()))
                .andExpect(status().isNoContent());

        // Verify it's deleted
        mockMvc.perform(get("/api/todos/" + createdTodo.getId()))
                .andExpect(status().isNotFound());
    }
}