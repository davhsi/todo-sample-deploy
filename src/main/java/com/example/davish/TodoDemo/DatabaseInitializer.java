package com.example.davish.TodoDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Initializing database...");
        
        try {
            // Check if the table exists by trying to count records
            long count = todoItemRepository.count();
            logger.info("Database initialized successfully. Found {} existing tasks.", count);
        } catch (Exception e) {
            logger.error("Database initialization failed: {}", e.getMessage());
            throw e;
        }
    }
} 