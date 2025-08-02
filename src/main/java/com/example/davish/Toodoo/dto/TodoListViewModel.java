package com.example.davish.Toodoo.dto;

import com.example.davish.Toodoo.entity.TodoItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TodoListViewModel {
    
    private ArrayList<TodoItem> todoList;
    
    // Filter parameters
    private String searchTerm;
    private String filterCategory;
    private String filterPriority;
    private String filterStatus;
    private boolean showCompleted = true;
    
    // Statistics
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private int overdueTasks;
    private Map<String, Long> categoryStats = new HashMap<>();
    private Map<String, Long> priorityStats = new HashMap<>();
    
    public TodoListViewModel() {
        this.todoList = new ArrayList<>();
    }
    
    public TodoListViewModel(ArrayList<TodoItem> todoList) {
        this.todoList = todoList;
    }
    
    public ArrayList<TodoItem> getTodoList() {
        return todoList;
    }
    
    public void setTodoList(ArrayList<TodoItem> todoList) {
        this.todoList = todoList;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public String getFilterCategory() {
        return filterCategory;
    }
    
    public void setFilterCategory(String filterCategory) {
        this.filterCategory = filterCategory;
    }
    
    public String getFilterPriority() {
        return filterPriority;
    }
    
    public void setFilterPriority(String filterPriority) {
        this.filterPriority = filterPriority;
    }
    
    public String getFilterStatus() {
        return filterStatus;
    }
    
    public void setFilterStatus(String filterStatus) {
        this.filterStatus = filterStatus;
    }
    
    public boolean isShowCompleted() {
        return showCompleted;
    }
    
    public void setShowCompleted(boolean showCompleted) {
        this.showCompleted = showCompleted;
    }
    
    public int getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public int getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }
    
    public int getPendingTasks() {
        return pendingTasks;
    }
    
    public void setPendingTasks(int pendingTasks) {
        this.pendingTasks = pendingTasks;
    }
    
    public int getOverdueTasks() {
        return overdueTasks;
    }
    
    public void setOverdueTasks(int overdueTasks) {
        this.overdueTasks = overdueTasks;
    }
    
    public Map<String, Long> getCategoryStats() {
        return categoryStats;
    }
    
    public void setCategoryStats(Map<String, Long> categoryStats) {
        this.categoryStats = categoryStats;
    }
    
    public Map<String, Long> getPriorityStats() {
        return priorityStats;
    }
    
    public void setPriorityStats(Map<String, Long> priorityStats) {
        this.priorityStats = priorityStats;
    }
} 