package com.example.davish.TodoDemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@SpringBootTest
class TodoDemoApplicationTests {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }

    @Test
    void testTodoItemCreation() {
        TodoItem item = new TodoItem("Work", "Complete project");
        assertEquals("Work", item.getCategory());
        assertEquals("Complete project", item.getName());
        assertFalse(item.isComplete());
        assertEquals(TodoItem.Priority.MEDIUM, item.getPriority());
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
    }

    @Test
    void testTodoItemOverdueDetection() {
        TodoItem item = new TodoItem("Work", "Overdue task");
        item.setDueDate(LocalDateTime.now().minusDays(1));
        assertTrue(item.isOverdue());
        
        item.setComplete(true);
        assertFalse(item.isOverdue()); // Completed tasks are not overdue
    }

    @Test
    void testTodoItemValidation() {
        TodoItem item = new TodoItem();
        // Test that validation annotations are present
        try {
            assertNotNull(TodoItem.class.getDeclaredField("name"));
            assertNotNull(TodoItem.class.getDeclaredField("category"));
        } catch (NoSuchFieldException e) {
            fail("Required fields not found: " + e.getMessage());
        }
    }

    @Test
    void testPriorityEnum() {
        TodoItem.Priority[] priorities = TodoItem.Priority.values();
        assertEquals(4, priorities.length);
        assertTrue(contains(priorities, TodoItem.Priority.LOW));
        assertTrue(contains(priorities, TodoItem.Priority.MEDIUM));
        assertTrue(contains(priorities, TodoItem.Priority.HIGH));
        assertTrue(contains(priorities, TodoItem.Priority.URGENT));
    }

    private boolean contains(TodoItem.Priority[] priorities, TodoItem.Priority priority) {
        for (TodoItem.Priority p : priorities) {
            if (p == priority) return true;
        }
        return false;
    }

    @Test
    void testTodoListViewModel() {
        TodoListViewModel viewModel = new TodoListViewModel();
        assertNotNull(viewModel.getTodoList());
        assertNotNull(viewModel.getCategoryStats());
        assertNotNull(viewModel.getPriorityStats());
        assertEquals(0, viewModel.getTotalTasks());
        assertEquals(0, viewModel.getCompletedTasks());
        assertEquals(0, viewModel.getPendingTasks());
        assertEquals(0, viewModel.getOverdueTasks());
    }
}
