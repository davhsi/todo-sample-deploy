package com.example.davish.TodoDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TodoDemoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoDemoController.class);

    @Autowired
    private TodoItemRepository repository;

    @RequestMapping("/")
    public String index(Model model, 
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String priority,
                       @RequestParam(required = false) String status,
                       @RequestParam(defaultValue = "true") boolean showCompleted) {
        
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
            
            model.addAttribute("items", viewModel);
            model.addAttribute("newitem", new TodoItem());
            model.addAttribute("categories", getDistinctCategories(allItems));
            model.addAttribute("priorities", TodoItem.Priority.values());
            
            logger.info("Successfully loaded {} tasks", filteredItems.size());
            
        } catch (Exception e) {
            logger.error("Error loading tasks", e);
            model.addAttribute("errorMessage", "Failed to load tasks. Please try again.");
        }
        
        return "index";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addTodo(@Valid @ModelAttribute("newitem") TodoItem requestItem, 
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors: ");
            bindingResult.getFieldErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage.toString());
            redirectAttributes.addFlashAttribute("errorType", "validation");
            
            // Ensure we have a valid model for the redirect
            try {
                List<TodoItem> allItems = new ArrayList<>();
                repository.findAll().forEach(allItems::add);
                TodoListViewModel viewModel = new TodoListViewModel((ArrayList<TodoItem>) allItems);
                calculateStatistics(viewModel);
                redirectAttributes.addFlashAttribute("items", viewModel);
                redirectAttributes.addFlashAttribute("newitem", new TodoItem());
            } catch (Exception e) {
                logger.error("Error preparing model for redirect", e);
            }
            
            return "redirect:/";
        }
        
        try {
            TodoItem item = new TodoItem(requestItem.getCategory(), requestItem.getName());
            item.setDescription(requestItem.getDescription());
            item.setPriority(requestItem.getPriority());
            item.setDueDate(requestItem.getDueDate());
            
            repository.save(item);
            
            redirectAttributes.addFlashAttribute("successMessage", "Task added successfully!");
            logger.info("Added new task: {}", item.getName());
            
        } catch (Exception e) {
            logger.error("Error adding task", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add task. Please try again.");
            
            // Ensure we have a valid model for the redirect even on error
            try {
                List<TodoItem> allItems = new ArrayList<>();
                repository.findAll().forEach(allItems::add);
                TodoListViewModel viewModel = new TodoListViewModel((ArrayList<TodoItem>) allItems);
                calculateStatistics(viewModel);
                redirectAttributes.addFlashAttribute("items", viewModel);
                redirectAttributes.addFlashAttribute("newitem", new TodoItem());
            } catch (Exception ex) {
                logger.error("Error preparing model for redirect", ex);
            }
        }
        
        return "redirect:/";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateTodo(@ModelAttribute TodoListViewModel requestItems,
                           RedirectAttributes redirectAttributes) {
        
        try {
            int updatedCount = 0;
            for (TodoItem requestItem : requestItems.getTodoList()) {
                if (requestItem.getId() != null) {
                    TodoItem item = repository.findById(requestItem.getId()).orElse(null);
                    if (item != null) {
                        item.setName(requestItem.getName());
                        item.setCategory(requestItem.getCategory());
                        item.setDescription(requestItem.getDescription());
                        item.setComplete(requestItem.isComplete());
                        item.setPriority(requestItem.getPriority());
                        item.setDueDate(requestItem.getDueDate());
                        
                        repository.save(item);
                        updatedCount++;
                    }
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                String.format("Successfully updated %d task(s)!", updatedCount));
            logger.info("Updated {} tasks", updatedCount);
            
        } catch (Exception e) {
            logger.error("Error updating tasks", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update tasks. Please try again.");
        }
        
        return "redirect:/";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deleteTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
                logger.info("Deleted task with ID: {}", id);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Task not found.");
            }
            
        } catch (Exception e) {
            logger.error("Error deleting task", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete task. Please try again.");
        }
        
        return "redirect:/";
    }

    @RequestMapping(value = "/bulk-delete", method = RequestMethod.POST)
    public String bulkDelete(@RequestParam("ids") List<Long> ids, RedirectAttributes redirectAttributes) {
        
        try {
            int deletedCount = 0;
            for (Long id : ids) {
                if (repository.existsById(id)) {
                    repository.deleteById(id);
                    deletedCount++;
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                String.format("Successfully deleted %d task(s)!", deletedCount));
            logger.info("Bulk deleted {} tasks", deletedCount);
            
        } catch (Exception e) {
            logger.error("Error bulk deleting tasks", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete tasks. Please try again.");
        }
        
        return "redirect:/";
    }

    @RequestMapping(value = "/toggle/{id}", method = RequestMethod.POST)
    @ResponseBody
    public String toggleComplete(@PathVariable Long id) {
        
        try {
            TodoItem item = repository.findById(id).orElse(null);
            if (item != null) {
                item.setComplete(!item.isComplete());
                repository.save(item);
                return "success";
            }
            return "not_found";
            
        } catch (Exception e) {
            logger.error("Error toggling task completion", e);
            return "error";
        }
    }

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

    private void calculateStatistics(TodoListViewModel viewModel) {
        try {
            List<TodoItem> allItems = new ArrayList<>();
            repository.findAll().forEach(allItems::add);
            
            viewModel.setTotalTasks(allItems.size());
            viewModel.setCompletedTasks(repository.countCompletedTasks());
            viewModel.setPendingTasks(repository.countPendingTasks());
            viewModel.setOverdueTasks(repository.countOverdueTasks(LocalDateTime.now()));
            
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

    private List<String> getDistinctCategories(List<TodoItem> items) {
        return items.stream()
                .map(TodoItem::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
