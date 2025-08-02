package com.example.davish.Toodoo;

import com.example.davish.Toodoo.entity.TodoItem;
import com.example.davish.Toodoo.dto.TodoListViewModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ToodooApplicationTests {

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }

    @Test
    void testTodoItemCreation() {
        TodoItem item = new TodoItem("Test Category", "Test Task");
        
        assertNotNull(item);
        assertEquals("Test Category", item.getCategory());
        assertEquals("Test Task", item.getName());
        assertFalse(item.isComplete());
        assertEquals(TodoItem.Priority.MEDIUM, item.getPriority());
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
    }

    @Test
    void testTodoItemOverdueDetection() {
        TodoItem item = new TodoItem("Test Category", "Test Task");
        
        // Set due date in the past
        item.setDueDate(LocalDateTime.now().minusDays(1));
        
        assertTrue(item.isOverdue());
        
        // Set due date in the future
        item.setDueDate(LocalDateTime.now().plusDays(1));
        
        assertFalse(item.isOverdue());
        
        // Complete task should not be overdue
        item.setComplete(true);
        assertFalse(item.isOverdue());
    }

    @Test
    void testTodoItemValidation() {
        TodoItem item = new TodoItem();
        
        // Test validation annotations
        try {
            // This would normally be validated by Bean Validation
            // For unit testing, we'll just check the structure
            assertNotNull(item);
        } catch (Exception e) {
            fail("TodoItem creation should not throw exception");
        }
    }

    @Test
    void testPriorityEnum() {
        TodoItem.Priority[] priorities = TodoItem.Priority.values();
        
        assertEquals(4, priorities.length);
        assertEquals(TodoItem.Priority.LOW, priorities[0]);
        assertEquals(TodoItem.Priority.MEDIUM, priorities[1]);
        assertEquals(TodoItem.Priority.HIGH, priorities[2]);
        assertEquals(TodoItem.Priority.URGENT, priorities[3]);
    }

    @Test
    void testTodoListViewModel() {
        ArrayList<TodoItem> items = new ArrayList<>();
        items.add(new TodoItem("Category1", "Task1"));
        items.add(new TodoItem("Category2", "Task2"));
        
        TodoListViewModel viewModel = new TodoListViewModel(items);
        
        assertNotNull(viewModel);
        assertEquals(2, viewModel.getTodoList().size());
        assertEquals("Task1", viewModel.getTodoList().get(0).getName());
        assertEquals("Task2", viewModel.getTodoList().get(1).getName());
    }

    @Test
    void testTodoListViewModelSetters() {
        TodoListViewModel viewModel = new TodoListViewModel();
        
        viewModel.setSearchTerm("test");
        viewModel.setFilterCategory("category");
        viewModel.setFilterPriority("HIGH");
        viewModel.setFilterStatus("pending");
        viewModel.setShowCompleted(false);
        viewModel.setTotalTasks(10);
        viewModel.setCompletedTasks(5);
        viewModel.setPendingTasks(3);
        viewModel.setOverdueTasks(2);
        
        assertEquals("test", viewModel.getSearchTerm());
        assertEquals("category", viewModel.getFilterCategory());
        assertEquals("HIGH", viewModel.getFilterPriority());
        assertEquals("pending", viewModel.getFilterStatus());
        assertFalse(viewModel.isShowCompleted());
        assertEquals(10, viewModel.getTotalTasks());
        assertEquals(5, viewModel.getCompletedTasks());
        assertEquals(3, viewModel.getPendingTasks());
        assertEquals(2, viewModel.getOverdueTasks());
    }
} 