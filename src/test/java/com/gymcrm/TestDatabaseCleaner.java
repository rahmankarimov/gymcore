package com.gymcrm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestDatabaseCleaner {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TestDatabaseCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clean() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        List.of("trainee_trainers", "trainings", "trainees", "trainers", "users")
                .forEach(table -> jdbcTemplate.execute("TRUNCATE TABLE " + table));
        List.of("trainings", "training_types", "users")
                .forEach(table -> jdbcTemplate.execute("ALTER TABLE " + table + " ALTER COLUMN id RESTART WITH 1"));
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
