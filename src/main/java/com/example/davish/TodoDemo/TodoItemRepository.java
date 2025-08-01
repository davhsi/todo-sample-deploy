package com.example.davish.TodoDemo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoItemRepository extends CrudRepository<TodoItem, Long> {

    List<TodoItem> findByCategory(String category);
    
    List<TodoItem> findByComplete(boolean complete);
    
    List<TodoItem> findByPriority(TodoItem.Priority priority);
    
    List<TodoItem> findByCategoryAndComplete(String category, boolean complete);
    
    @Query("SELECT t FROM TodoItem t WHERE t.name LIKE %:searchTerm% OR t.description LIKE %:searchTerm% OR t.category LIKE %:searchTerm%")
    List<TodoItem> searchByTerm(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT t FROM TodoItem t WHERE t.dueDate < :now AND t.complete = false")
    List<TodoItem> findOverdueTasks(@Param("now") LocalDateTime now);
    
    @Query("SELECT t FROM TodoItem t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<TodoItem> findTasksByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM TodoItem t WHERE t.complete = true")
    long countCompletedTasks();
    
    @Query("SELECT COUNT(t) FROM TodoItem t WHERE t.complete = false")
    long countPendingTasks();
    
    @Query("SELECT COUNT(t) FROM TodoItem t WHERE t.dueDate < :now AND t.complete = false")
    long countOverdueTasks(@Param("now") LocalDateTime now);
    
    @Query("SELECT t.category, COUNT(t) FROM TodoItem t GROUP BY t.category")
    List<Object[]> getTaskCountByCategory();
    
    @Query("SELECT t.priority, COUNT(t) FROM TodoItem t WHERE t.complete = false GROUP BY t.priority")
    List<Object[]> getPendingTaskCountByPriority();
}