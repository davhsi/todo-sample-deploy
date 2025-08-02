package com.example.davish.Toodoo.controller;

import com.example.davish.Toodoo.entity.TodoItem;
import com.example.davish.Toodoo.service.TodoService;
import com.example.davish.Toodoo.dto.TodoListViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    @Autowired
    private TodoService todoService;

    @RequestMapping("/")
    public String index(Model model, 
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String priority,
                       @RequestParam(required = false) String status,
                       @RequestParam(defaultValue = "true") boolean showCompleted) {
        
        try {
            TodoListViewModel viewModel = todoService.getTasksWithFilters(search, category, priority, status, showCompleted);
            
            model.addAttribute("items", viewModel);
            model.addAttribute("newitem", new TodoItem());
            model.addAttribute("categories", todoService.getDistinctCategories());
            model.addAttribute("priorities", TodoItem.Priority.values());
            
        } catch (Exception e) {
            logger.error("Error loading tasks", e);
            model.addAttribute("errorMessage", "Failed to load tasks. Please try again.");
        }
        
        return "index";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addTodo(@Valid @ModelAttribute("newitem") TodoItem requestItem, 
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors: ");
            bindingResult.getFieldErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            });
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage.toString());
            return "redirect:/";
        }
        
        try {
            todoService.addTask(requestItem);
            redirectAttributes.addFlashAttribute("successMessage", "Task added successfully!");
            
        } catch (Exception e) {
            logger.error("Error adding task", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add task. Please try again.");
        }
        
        return "redirect:/";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String updateTodo(@ModelAttribute TodoListViewModel requestItems,
                           RedirectAttributes redirectAttributes) {
        
        try {
            todoService.updateTasks(requestItems.getTodoList());
            redirectAttributes.addFlashAttribute("successMessage", "Tasks updated successfully!");
            
        } catch (Exception e) {
            logger.error("Error updating tasks", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update tasks. Please try again.");
        }
        
        return "redirect:/";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public String deleteTodo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        
        try {
            todoService.deleteTask(id);
            redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
            
        } catch (Exception e) {
            logger.error("Error deleting task", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete task. Please try again.");
        }
        
        return "redirect:/";
    }

    @RequestMapping(value = "/bulk-delete", method = RequestMethod.POST)
    public String bulkDelete(@RequestParam("ids") List<Long> ids, RedirectAttributes redirectAttributes) {
        
        try {
            int deletedCount = todoService.bulkDeleteTasks(ids);
            redirectAttributes.addFlashAttribute("successMessage", 
                String.format("Successfully deleted %d task(s)!", deletedCount));
            
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
            boolean success = todoService.toggleTaskCompletion(id);
            return success ? "success" : "not_found";
            
        } catch (Exception e) {
            logger.error("Error toggling task completion", e);
            return "error";
        }
    }
} 