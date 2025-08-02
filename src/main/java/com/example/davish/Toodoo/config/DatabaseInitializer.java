package com.example.davish.Toodoo.config;

import com.example.davish.Toodoo.repository.TodoItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private TodoItemRepository repository;

    @Override
    public void run(String... args) throws Exception {
        try {
            long taskCount = repository.count();
            logger.info("Database initialized successfully. Found {} existing tasks", taskCount);
        } catch (Exception e) {
            logger.error("Error initializing database", e);
        }
    }
} 