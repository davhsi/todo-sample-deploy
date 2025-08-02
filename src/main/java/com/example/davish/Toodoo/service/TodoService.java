package com.example.davish.Toodoo.service;

import com.example.davish.Toodoo.entity.TodoItem;
import com.example.davish.Toodoo.repository.TodoItemRepository;
import com.example.davish.Toodoo.dto.TodoListViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TodoService {

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    @Autowired
    private TodoItemRepository repository;

    /**
     * Get all tasks with filtering and statistics
     */
    public TodoListViewModel getTasksWithFilters(String search, String category, 
                                               String priority, String status, boolean showCompleted) {
        try {
            List<TodoItem> allItems = new ArrayList<>();
            repository.findAll().forEach(allItems::add);
            
            // Apply filters
            List<TodoItem> filteredItems = filterItems(allItems, search, category, priority, status, showCompleted);
            
            // Create view model
            TodoListViewModel viewModel = new TodoListViewModel((ArrayList<TodoItem>) filteredItems);
            viewModel.setSearchTerm(search);
            viewModel.setFilterCategory(category);
            viewModel.setFilterPriority(priority);
            viewModel.setFilterStatus(status);
            viewModel.setShowCompleted(showCompleted);
            
            // Calculate statistics
            calculateStatistics(viewModel);
            
            logger.info("Successfully loaded {} tasks", filteredItems.size());
            return viewModel;
            
        } catch (Exception e) {
            logger.error("Error loading tasks", e);
            throw new RuntimeException("Failed to load tasks", e);
        }
    }

    /**
     * Add a new task
     */
    public void addTask(TodoItem task) {
        try {
            TodoItem savedTask = repository.save(task);
            logger.info("Successfully added task: {}", savedTask.getName());
        } catch (Exception e) {
            logger.error("Error adding task", e);
            throw new RuntimeException("Failed to add task", e);
        }
    }

    /**
     * Update existing tasks
     */
    public void updateTasks(List<TodoItem> tasks) {
        try {
            int updatedCount = 0;
            for (TodoItem task : tasks) {
                if (task.getId() != null && repository.existsById(task.getId())) {
                    repository.save(task);
                    updatedCount++;
                }
            }
            logger.info("Successfully updated {} tasks", updatedCount);
        } catch (Exception e) {
            logger.error("Error updating tasks", e);
            throw new RuntimeException("Failed to update tasks", e);
        }
    }

    /**
     * Delete a single task
     */
    public void deleteTask(Long id) {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                logger.info("Successfully deleted task with id: {}", id);
            } else {
                logger.warn("Task with id {} not found for deletion", id);
            }
        } catch (Exception e) {
            logger.error("Error deleting task", e);
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    /**
     * Bulk delete tasks
     */
    public int bulkDeleteTasks(List<Long> ids) {
        try {
            int deletedCount = 0;
            for (Long id : ids) {
                if (repository.existsById(id)) {
                    repository.deleteById(id);
                    deletedCount++;
                }
            }
            logger.info("Bulk deleted {} tasks", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            logger.error("Error bulk deleting tasks", e);
            throw new RuntimeException("Failed to delete tasks", e);
        }
    }

    /**
     * Toggle task completion status
     */
    public boolean toggleTaskCompletion(Long id) {
        try {
            TodoItem item = repository.findById(id).orElse(null);
            if (item != null) {
                item.setComplete(!item.isComplete());
                repository.save(item);
                logger.info("Toggled completion for task: {}", item.getName());
                return true;
            }
            logger.warn("Task with id {} not found for toggle", id);
            return false;
        } catch (Exception e) {
            logger.error("Error toggling task completion", e);
            throw new RuntimeException("Failed to toggle task completion", e);
        }
    }

    /**
     * Get distinct categories
     */
    public List<String> getDistinctCategories() {
        try {
            List<TodoItem> allItems = new ArrayList<>();
            repository.findAll().forEach(allItems::add);
            
            return allItems.stream()
                    .map(TodoItem::getCategory)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting distinct categories", e);
            return new ArrayList<>();
        }
    }

    /**
     * Filter items based on criteria
     */
    private List<TodoItem> filterItems(List<TodoItem> items, String search, String category, 
                                     String priority, String status, boolean showCompleted) {
        
        return items.stream()
                .filter(item -> search == null || search.isEmpty() || 
                        item.getName().toLowerCase().contains(search.toLowerCase()) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(search.toLowerCase())) ||
                        item.getCategory().toLowerCase().contains(search.toLowerCase()))
                .filter(item -> category == null || category.isEmpty() || 
                        item.getCategory().equals(category))
                .filter(item -> priority == null || priority.isEmpty() || 
                        item.getPriority().toString().equals(priority))
                .filter(item -> status == null || status.isEmpty() || 
                        (status.equals("completed") && item.isComplete()) ||
                        (status.equals("pending") && !item.isComplete()) ||
                        (status.equals("overdue") && item.isOverdue()))
                .filter(item -> showCompleted || !item.isComplete())
                .collect(Collectors.toList());
    }

    /**
     * Calculate statistics for the view model
     */
    private void calculateStatistics(TodoListViewModel viewModel) {
        try {
            List<TodoItem> allItems = new ArrayList<>();
            repository.findAll().forEach(allItems::add);
            
            viewModel.setTotalTasks(allItems.size());
            viewModel.setCompletedTasks((int) repository.countCompletedTasks());
            viewModel.setPendingTasks((int) repository.countPendingTasks());
            viewModel.setOverdueTasks((int) repository.countOverdueTasks(LocalDateTime.now()));
            
            // Category statistics
            Map<String, Long> categoryStats = new HashMap<>();
            repository.getTaskCountByCategory().forEach(result -> {
                categoryStats.put((String) result[0], (Long) result[1]);
            });
            viewModel.setCategoryStats(categoryStats);
            
            // Priority statistics
            Map<String, Long> priorityStats = new HashMap<>();
            repository.getPendingTaskCountByPriority().forEach(result -> {
                priorityStats.put(result[0].toString(), (Long) result[1]);
            });
            viewModel.setPriorityStats(priorityStats);
            
        } catch (Exception e) {
            logger.error("Error calculating statistics", e);
        }
    }
} 