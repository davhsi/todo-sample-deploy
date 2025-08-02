// Toodoo Application JavaScript

// Initialize application when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

function initializeApp() {
    updateTime();
    setInterval(updateTime, 1000);
    
    // Auto-hide toasts after 5 seconds
    setTimeout(function() {
        $('.toast').toast('hide');
    }, 5000);
    
    // Initialize form validation
    initializeFormValidation();
    
    // Initialize search functionality
    initializeSearch();
}

// Update current time
function updateTime() {
    const now = new Date();
    const timeElement = document.getElementById('current-time');
    if (timeElement) {
        timeElement.textContent = now.toLocaleTimeString();
    }
}

// Toggle task completion
function toggleTask(checkbox, taskId) {
    fetch(`/toggle/${taskId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.text())
    .then(result => {
        if (result === 'success') {
            const taskItem = checkbox.closest('.task-item');
            if (checkbox.checked) {
                taskItem.classList.add('task-complete');
            } else {
                taskItem.classList.remove('task-complete');
            }
        } else {
            checkbox.checked = !checkbox.checked; // Revert if failed
            showToast('Error toggling task', 'error');
        }
    })
    .catch(error => {
        checkbox.checked = !checkbox.checked; // Revert if failed
        showToast('Error toggling task', 'error');
    });
}

// Delete task
function deleteTask(taskId) {
    if (confirm('Are you sure you want to delete this task?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `/delete/${taskId}`;
        document.body.appendChild(form);
        form.submit();
    }
}

// Bulk delete tasks
function bulkDelete() {
    const selectedIds = Array.from(document.querySelectorAll('.task-checkbox:checked'))
        .map(checkbox => checkbox.getAttribute('data-task-id'));
    
    if (selectedIds.length === 0) {
        showToast('No tasks selected', 'warning');
        return;
    }

    if (confirm(`Are you sure you want to delete ${selectedIds.length} task(s)?`)) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/bulk-delete';
        
        selectedIds.forEach(id => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'ids';
            input.value = id;
            form.appendChild(input);
        });
        
        document.body.appendChild(form);
        form.submit();
    }
}

// Show toast notification
function showToast(message, type) {
    const toastHtml = `
        <div class="toast show" role="alert">
            <div class="toast-header">
                <i class="fas fa-${type === 'error' ? 'exclamation-circle' : type === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
                <strong class="me-auto">${type.charAt(0).toUpperCase() + type.slice(1)}</strong>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>
            </div>
            <div class="toast-body">${message}</div>
        </div>
    `;
    
    const container = document.querySelector('.toast-container');
    if (container) {
        container.insertAdjacentHTML('beforeend', toastHtml);
        
        setTimeout(() => {
            const toast = container.lastElementChild;
            if (toast) {
                toast.remove();
            }
        }, 5000);
    }
}

// Initialize form validation
function initializeFormValidation() {
    const addTaskForm = document.getElementById('addTaskForm');
    if (addTaskForm) {
        addTaskForm.addEventListener('submit', function(e) {
            const name = this.querySelector('[name="name"]').value.trim();
            const category = this.querySelector('[name="category"]').value.trim();
            
            if (!name || !category) {
                e.preventDefault();
                showToast('Please fill in all required fields', 'error');
                return false;
            }
        });
    }
}

// Initialize search functionality
function initializeSearch() {
    const searchInput = document.querySelector('input[name="search"]');
    if (searchInput) {
        let searchTimeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                document.getElementById('filterForm').submit();
            }, 500);
        });
    }
}

// Utility function to format date
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Utility function to check if task is overdue
function isOverdue(dueDate) {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date();
}

// Export functions for global access
window.Toodoo = {
    toggleTask,
    deleteTask,
    bulkDelete,
    showToast,
    formatDate,
    isOverdue
}; 