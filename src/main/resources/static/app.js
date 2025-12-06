const API_BASE = 'http://localhost:8080/api';
let currentUserId = 1;
let currentFilter = 'all';

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    updateCurrentDate();
    setInterval(updateCurrentDate, 60000); // Update every minute
    loadDashboard();
});

function updateCurrentDate() {
    const now = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('currentDate').textContent = now.toLocaleDateString('en-US', options);
}

async function loadDashboard() {
    await Promise.all([
        loadRecommendedTasks(),
        loadAllTasks(),
        loadCalendarEvents(),
        loadNotes(),
        loadStats()
    ]);
}

// Task Functions
async function loadRecommendedTasks() {
    try {
        const response = await fetch(`${API_BASE}/tasks/recommended?userId=${currentUserId}`);
        const tasks = await response.json();
        displayRecommendedTasks(tasks);
    } catch (error) {
        console.error('Error loading recommended tasks:', error);
    }
}

function displayRecommendedTasks(tasks) {
    const container = document.getElementById('recommendedTasks');

    if (tasks.length === 0) {
        container.innerHTML = '<div class="empty-state">No recommended tasks. Great job!</div>';
        return;
    }

    container.innerHTML = tasks.map((task, index) => `
        <div class="task-item recommended">
            <div class="task-header">
                <div class="task-title">${index + 1}. ${task.title}</div>
                <span class="task-priority priority-${task.priority}">${task.priority}</span>
            </div>
            ${task.description ? `<div class="task-description">${task.description}</div>` : ''}
            <div class="task-meta">
                ${task.dueDate ? `<span ${task.isOverdue ? 'class="overdue"' : ''}>Due: ${formatDate(task.dueDate)}</span>` : ''}
                ${task.estimatedDuration ? `<span>~${task.estimatedDuration} min</span>` : ''}
                <span class="task-score">Score: ${task.recommendationScore.toFixed(1)}</span>
            </div>
            <div class="task-actions">
                <button class="btn-complete" onclick="completeTask(${task.id})">Complete</button>
                <button class="btn-delete" onclick="deleteTask(${task.id})">Delete</button>
            </div>
        </div>
    `).join('');
}

async function loadAllTasks() {
    try {
        const response = await fetch(`${API_BASE}/tasks?userId=${currentUserId}`);
        const tasks = await response.json();
        displayAllTasks(tasks);
    } catch (error) {
        console.error('Error loading tasks:', error);
    }
}

function displayAllTasks(tasks) {
    const container = document.getElementById('allTasks');

    const filteredTasks = currentFilter === 'all'
        ? tasks
        : tasks.filter(task => task.status === currentFilter);

    if (filteredTasks.length === 0) {
        container.innerHTML = '<div class="empty-state">No tasks found</div>';
        return;
    }

    container.innerHTML = filteredTasks.map(task => `
        <div class="task-item">
            <div class="task-header">
                <div class="task-title">${task.title}</div>
                <span class="task-priority priority-${task.priority}">${task.priority}</span>
            </div>
            ${task.description ? `<div class="task-description">${task.description}</div>` : ''}
            <div class="task-meta">
                <span>Status: ${task.status.replace('_', ' ')}</span>
                ${task.dueDate ? `<span ${task.isOverdue ? 'class="overdue"' : ''}>Due: ${formatDate(task.dueDate)}</span>` : ''}
                ${task.estimatedDuration ? `<span>~${task.estimatedDuration} min</span>` : ''}
            </div>
            ${task.status !== 'COMPLETED' ? `
                <div class="task-actions">
                    <button class="btn-complete" onclick="completeTask(${task.id})">Complete</button>
                    <button class="btn-delete" onclick="deleteTask(${task.id})">Delete</button>
                </div>
            ` : ''}
        </div>
    `).join('');
}

function filterTasks(filter) {
    currentFilter = filter;

    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    loadAllTasks();
}

async function submitTask(event) {
    event.preventDefault();

    const taskData = {
        title: document.getElementById('taskTitle').value,
        description: document.getElementById('taskDescription').value,
        priority: document.getElementById('taskPriority').value,
        estimatedDuration: document.getElementById('taskDuration').value || null,
        dueDate: document.getElementById('taskDueDate').value || null
    };

    try {
        const response = await fetch(`${API_BASE}/tasks?userId=${currentUserId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(taskData)
        });

        if (response.ok) {
            closeTaskModal();
            document.getElementById('taskForm').reset();
            loadDashboard();
        }
    } catch (error) {
        console.error('Error creating task:', error);
        alert('Failed to create task');
    }
}

async function completeTask(taskId) {
    const duration = prompt('How many minutes did it take? (optional)');

    try {
        const response = await fetch(`${API_BASE}/tasks/${taskId}/complete`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                actualDuration: duration ? parseInt(duration) : null
            })
        });

        if (response.ok) {
            loadDashboard();
        }
    } catch (error) {
        console.error('Error completing task:', error);
    }
}

async function deleteTask(taskId) {
    if (!confirm('Are you sure you want to delete this task?')) return;

    try {
        const response = await fetch(`${API_BASE}/tasks/${taskId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadDashboard();
        }
    } catch (error) {
        console.error('Error deleting task:', error);
    }
}

// Calendar Functions
async function loadCalendarEvents() {
    try {
        const response = await fetch(`${API_BASE}/calendar/today`);
        const events = await response.json();
        displayCalendarEvents(events);
    } catch (error) {
        console.error('Error loading calendar events:', error);
        document.getElementById('calendarEvents').innerHTML =
            '<div class="empty-state">Calendar not configured</div>';
    }
}

function displayCalendarEvents(events) {
    const container = document.getElementById('calendarEvents');

    if (events.length === 0) {
        container.innerHTML = '<div class="empty-state">No events today</div>';
        return;
    }

    container.innerHTML = events.map(event => `
        <div class="event-item">
            <div class="event-time">${formatTime(event.startTime)} - ${formatTime(event.endTime)}</div>
            <div class="event-title">${event.summary}</div>
            ${event.location ? `<div class="event-location">📍 ${event.location}</div>` : ''}
        </div>
    `).join('');
}

function refreshCalendar() {
    loadCalendarEvents();
}

// Note Functions
async function loadNotes() {
    try {
        const response = await fetch(`${API_BASE}/notes?userId=${currentUserId}`);
        const notes = await response.json();
        displayNotes(notes);
    } catch (error) {
        console.error('Error loading notes:', error);
    }
}

function displayNotes(notes) {
    const container = document.getElementById('notesList');

    if (notes.length === 0) {
        container.innerHTML = '<div class="empty-state">No notes yet</div>';
        return;
    }

    container.innerHTML = notes.map(note => `
        <div class="note-item ${note.pinned ? 'pinned' : ''}">
            <div class="note-title">${note.pinned ? '📌 ' : ''}${note.title}</div>
            <div class="note-content">${note.content || ''}</div>
            ${note.tags && note.tags.length > 0 ? `
                <div class="note-tags">
                    ${note.tags.map(tag => `<span class="tag">${tag}</span>`).join('')}
                </div>
            ` : ''}
        </div>
    `).join('');
}

async function submitNote(event) {
    event.preventDefault();

    const tags = document.getElementById('noteTags').value
        .split(',')
        .map(tag => tag.trim())
        .filter(tag => tag.length > 0);

    const noteData = {
        title: document.getElementById('noteTitle').value,
        content: document.getElementById('noteContent').value,
        tags: tags,
        pinned: document.getElementById('notePinned').checked
    };

    try {
        const response = await fetch(`${API_BASE}/notes?userId=${currentUserId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(noteData)
        });

        if (response.ok) {
            closeNoteModal();
            document.getElementById('noteForm').reset();
            loadNotes();
            loadStats();
        }
    } catch (error) {
        console.error('Error creating note:', error);
        alert('Failed to create note');
    }
}

async function searchNotes() {
    const query = document.getElementById('noteSearch').value;

    if (query.length < 2) {
        loadNotes();
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/notes/search?userId=${currentUserId}&query=${encodeURIComponent(query)}`);
        const notes = await response.json();
        displayNotes(notes);
    } catch (error) {
        console.error('Error searching notes:', error);
    }
}

// Stats Functions
async function loadStats() {
    try {
        const tasksResponse = await fetch(`${API_BASE}/tasks?userId=${currentUserId}`);
        const tasks = await tasksResponse.json();

        const notesResponse = await fetch(`${API_BASE}/notes?userId=${currentUserId}`);
        const notes = await notesResponse.json();

        document.getElementById('totalTasks').textContent = tasks.length;
        document.getElementById('completedTasks').textContent =
            tasks.filter(t => t.status === 'COMPLETED').length;
        document.getElementById('overdueTasks').textContent =
            tasks.filter(t => t.isOverdue).length;
        document.getElementById('totalNotes').textContent = notes.length;
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

// Modal Functions
function showAddTaskModal() {
    document.getElementById('taskModal').style.display = 'block';
}

function closeTaskModal() {
    document.getElementById('taskModal').style.display = 'none';
}

function showAddNoteModal() {
    document.getElementById('noteModal').style.display = 'block';
}

function closeNoteModal() {
    document.getElementById('noteModal').style.display = 'none';
}

// Close modal when clicking outside
window.onclick = function(event) {
    const taskModal = document.getElementById('taskModal');
    const noteModal = document.getElementById('noteModal');

    if (event.target === taskModal) {
        closeTaskModal();
    }
    if (event.target === noteModal) {
        closeNoteModal();
    }
}

// Utility Functions
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = date - now;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays < 0) {
        return 'OVERDUE';
    } else if (diffDays === 0) {
        return 'Today ' + date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
    } else if (diffDays === 1) {
        return 'Tomorrow ' + date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
    } else {
        return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }) +
               ' ' + date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
    }
}

function formatTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
}
