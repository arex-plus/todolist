package com.metabase.todolist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metabase.todolist.model.Todo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

public class TodoControllerIntegrationTest {

    private static final int PORT = 8080;
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getRootUrl() {
        return "http://localhost:" + PORT + "/api/todos";
    }

    @Test
    public void shouldCreateTodo() {
        Todo todo = new Todo("集成测试Todo", "集成测试描述");
        ResponseEntity<Todo> response = restTemplate.postForEntity(
                getRootUrl(),
                todo,
                Todo.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("集成测试Todo", response.getBody().getTitle());
        assertEquals("集成测试描述", response.getBody().getDescription());
        assertFalse(response.getBody().isCompleted());
    }

    @Test
    public void shouldGetTodoById() {
        // 先创建一个Todo
        Todo todo = new Todo("测试获取Todo", "测试获取描述");
        ResponseEntity<Todo> createResponse = restTemplate.postForEntity(
                getRootUrl(),
                todo,
                Todo.class
        );
        Todo createdTodo = createResponse.getBody();

        // 然后通过ID获取
        ResponseEntity<Todo> getResponse = restTemplate.getForEntity(
                getRootUrl() + "/" + createdTodo.getId(),
                Todo.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("测试获取Todo", getResponse.getBody().getTitle());
        assertEquals("测试获取描述", getResponse.getBody().getDescription());
    }

    @Test
    public void shouldGetAllTodos() {
        // 创建一个新的Todo
        Todo todo = new Todo("测试列表Todo", "测试列表描述");
        restTemplate.postForEntity(getRootUrl(), todo, Todo.class);

        // 获取所有Todo列表
        ResponseEntity<Todo[]> response = restTemplate.getForEntity(
                getRootUrl(),
                Todo[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    public void shouldUpdateTodo() {
        // 先创建一个Todo
        Todo todo = new Todo("原始Todo", "原始描述");
        ResponseEntity<Todo> createResponse = restTemplate.postForEntity(
                getRootUrl(),
                todo,
                Todo.class
        );
        Todo createdTodo = createResponse.getBody();

        // 更新Todo
        createdTodo.setTitle("更新后的Todo");
        createdTodo.setCompleted(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Todo> requestEntity = new HttpEntity<>(createdTodo, headers);

        ResponseEntity<Todo> updateResponse = restTemplate.exchange(
                getRootUrl() + "/" + createdTodo.getId(),
                HttpMethod.PUT,
                requestEntity,
                Todo.class
        );

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("更新后的Todo", updateResponse.getBody().getTitle());
        assertTrue(updateResponse.getBody().isCompleted());
    }

    @Test
    public void shouldDeleteTodo() {
        // 先创建一个Todo
        Todo todo = new Todo("待删除Todo", "待删除描述");
        ResponseEntity<Todo> createResponse = restTemplate.postForEntity(
                getRootUrl(),
                todo,
                Todo.class
        );
        Todo createdTodo = createResponse.getBody();

        // 删除Todo
        restTemplate.delete(getRootUrl() + "/" + createdTodo.getId());

        // 验证删除是否成功
        ResponseEntity<Todo> getResponse = restTemplate.getForEntity(
                getRootUrl() + "/" + createdTodo.getId(),
                Todo.class
        );

        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
}