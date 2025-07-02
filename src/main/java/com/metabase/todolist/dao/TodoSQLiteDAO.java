package com.metabase.todolist.dao;

import com.metabase.todolist.model.Todo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TodoSQLiteDAO {
    private final DataAccess dataAccess;
    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS todos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            description TEXT,
            completed BOOLEAN NOT NULL DEFAULT 0,
            created_at TIMESTAMP NOT NULL,
            updated_at TIMESTAMP NOT NULL
        )
        """;

    public TodoSQLiteDAO(JdbcTemplate jdbcTemplate) {
        this.dataAccess = new DataAccess(jdbcTemplate);
        initializeDatabase();
    }

    private void initializeDatabase() {
        dataAccess.execute(CREATE_TABLE_SQL);
    }

    public Todo create(Todo todo) {
        String sql = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        
        Long generatedId = dataAccess.insertAndReturnKey(sql, todo.getTitle(), todo.getDescription(), todo.isCompleted(), Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        
        if (generatedId != null) {
            todo.setId(generatedId);
            return todo;
        }
        return null;
    }

    public Todo findById(Long id) {
        String sql = "SELECT * FROM todos WHERE id = ?";
        return dataAccess.queryForObject(sql, Todo.class, id);
    }

    public List<Todo> findAll() {
        String sql = "SELECT * FROM todos";
        return dataAccess.query(sql, Todo.class);
    }

    public Todo update(Long id, Todo todo) {
        String sql = "UPDATE todos SET title = ?, description = ?, completed = ?, updated_at = ? WHERE id = ?";
        int affectedRows = dataAccess.execute(sql, todo.getTitle(), todo.getDescription(), 
            todo.isCompleted(), Timestamp.valueOf(LocalDateTime.now()), id);
        
        if (affectedRows > 0) {
            todo.setId(id);
            return todo;
        }
        return null;
    }

    public void delete(Long id) {
        String sql = "DELETE FROM todos WHERE id = ?";
        dataAccess.execute(sql, id);
    }

    private Todo mapResultSetToTodo(ResultSet rs) throws SQLException {
        Todo todo = new Todo();
        todo.setId(rs.getLong("id"));
        todo.setTitle(rs.getString("title"));
        todo.setDescription(rs.getString("description"));
        todo.setCompleted(rs.getBoolean("completed"));
        todo.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        todo.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return todo;
    }
}