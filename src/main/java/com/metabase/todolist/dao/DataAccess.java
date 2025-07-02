package com.metabase.todolist.dao;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.*;
import java.util.List;

public class DataAccess {
    protected final JdbcTemplate jdbcTemplate;

    public DataAccess(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int execute(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    public Long insertAndReturnKey(String sql, Object... args) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            return ps;
        }, keyHolder);
        return keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
    }


    public <T> T queryForObject(String sql, Class<T> clazz, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(clazz), args);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public <T> List<T> query(String sql, Class<T> clazz, Object... args) {
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(clazz), args);
    }
}