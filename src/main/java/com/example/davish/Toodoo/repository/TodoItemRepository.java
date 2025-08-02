package com.example.davish.Toodoo.repository;

import com.example.davish.Toodoo.entity.TodoItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoItemRepository extends CrudRepository<TodoItem, Long> {

    List<TodoItem> findByCategory(String category);
    
    List<TodoItem> findByComplete(boolean complete);
    
    List<TodoItem> findByPriority(TodoItem.Priority priority);
    
    List<TodoItem> findByCategoryAndComplete(String category, boolean complete);
    
    @Query("SELECT t FROM TodoItem t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(t.category) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<TodoItem> searchByTerm(String searchTerm);
    
    @Query("SELECT t FROM TodoItem t WHERE t.dueDate < ?1 AND t.complete = false")
    List<TodoItem> findOverdueTasks(LocalDateTime now);
    
    @Query("SELECT t FROM TodoItem t WHERE t.dueDate BETWEEN ?1 AND ?2")
    List<TodoItem> findTasksByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(t) FROM TodoItem t WHERE t.complete = true")
    long countCompletedTasks();
    
    @Query("SELECT COUNT(t) FROM TodoItem t WHERE t.complete = false")
    long countPendingTasks();
    
    @Query("SELECT COUNT(t) FROM TodoItem t WHERE t.dueDate < ?1 AND t.complete = false")
    long countOverdueTasks(LocalDateTime now);
    
    @Query("SELECT t.category, COUNT(t) FROM TodoItem t GROUP BY t.category")
    List<Object[]> getTaskCountByCategory();
    
    @Query("SELECT t.priority, COUNT(t) FROM TodoItem t WHERE t.complete = false GROUP BY t.priority")
    List<Object[]> getPendingTaskCountByPriority();
} 