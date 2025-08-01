package com.example.davish.TodoDemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.validation.Valid;

public class TodoListViewModel {

	@Valid
	private ArrayList<TodoItem> todoList = new ArrayList<TodoItem>();
	
	private String searchTerm;
	private String filterCategory;
	private String filterPriority;
	private String filterStatus;
	private boolean showCompleted = true;
	
	// Statistics
	private long totalTasks;
	private long completedTasks;
	private long pendingTasks;
	private long overdueTasks;
	private Map<String, Long> categoryStats = new HashMap<>();
	private Map<String, Long> priorityStats = new HashMap<>();
	
	public TodoListViewModel() {}
	
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

	public long getTotalTasks() {
		return totalTasks;
	}

	public void setTotalTasks(long totalTasks) {
		this.totalTasks = totalTasks;
	}

	public long getCompletedTasks() {
		return completedTasks;
	}

	public void setCompletedTasks(long completedTasks) {
		this.completedTasks = completedTasks;
	}

	public long getPendingTasks() {
		return pendingTasks;
	}

	public void setPendingTasks(long pendingTasks) {
		this.pendingTasks = pendingTasks;
	}

	public long getOverdueTasks() {
		return overdueTasks;
	}

	public void setOverdueTasks(long overdueTasks) {
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