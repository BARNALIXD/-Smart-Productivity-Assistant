package com.productivity.service;

import com.productivity.dto.TaskDTO;
import com.productivity.entity.Task;
import com.productivity.entity.TaskCompletionHistory;
import com.productivity.entity.User;
import com.productivity.repository.TaskCompletionHistoryRepository;
import com.productivity.repository.TaskRepository;
import com.productivity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskCompletionHistoryRepository historyRepository;
    private final RecommendationEngine recommendationEngine;

    @Transactional
    public TaskDTO createTask(Long userId, TaskDTO taskDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = new Task();
        task.setUser(user);
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority() != null ? taskDTO.getPriority() : Task.Priority.MEDIUM);
        task.setStatus(Task.Status.TODO);
        task.setDueDate(taskDTO.getDueDate());
        task.setEstimatedDuration(taskDTO.getEstimatedDuration());

        // Calculate initial recommendation score
        double score = recommendationEngine.calculateRecommendationScore(task, userId);
        task.setRecommendationScore(score);

        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    @Transactional
    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (taskDTO.getTitle() != null) task.setTitle(taskDTO.getTitle());
        if (taskDTO.getDescription() != null) task.setDescription(taskDTO.getDescription());
        if (taskDTO.getPriority() != null) task.setPriority(taskDTO.getPriority());
        if (taskDTO.getStatus() != null) task.setStatus(taskDTO.getStatus());
        if (taskDTO.getDueDate() != null) task.setDueDate(taskDTO.getDueDate());
        if (taskDTO.getEstimatedDuration() != null) task.setEstimatedDuration(taskDTO.getEstimatedDuration());

        // Recalculate recommendation score
        double score = recommendationEngine.calculateRecommendationScore(task, task.getUser().getId());
        task.setRecommendationScore(score);

        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Transactional
    public TaskDTO completeTask(Long taskId, Integer actualDuration) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setStatus(Task.Status.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setActualDuration(actualDuration);

        // Save to history for ML learning
        saveCompletionHistory(task);

        Task completedTask = taskRepository.save(task);
        return convertToDTO(completedTask);
    }

    private void saveCompletionHistory(Task task) {
        TaskCompletionHistory history = new TaskCompletionHistory();
        history.setUserId(task.getUser().getId());
        history.setTaskId(task.getId());
        history.setTaskTitle(task.getTitle());
        history.setPriority(task.getPriority());
        history.setEstimatedDuration(task.getEstimatedDuration());
        history.setActualDuration(task.getActualDuration());
        history.setCompletedAt(task.getCompletedAt());
        history.setDayOfWeek(task.getCompletedAt().getDayOfWeek());
        history.setHourOfDay(task.getCompletedAt().getHour());
        history.setWasOverdue(task.isOverdue());
        history.setTimeBeforeDue(task.getTimeUntilDue());
        history.setCreatedAt(LocalDateTime.now());

        historyRepository.save(history);
        log.info("Saved completion history for task: {}", task.getId());
    }

    public List<TaskDTO> getRecommendedTasks(Long userId) {
        List<Task> tasks = taskRepository.findActiveTasksByUserOrderByScore(userId);
        return tasks.stream()
                .limit(5) // Top 5 recommendations
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getAllTasks(Long userId) {
        List<Task> tasks = taskRepository.findByUserIdOrderByRecommendationScoreDesc(userId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByStatus(Long userId, Task.Status status) {
        List<Task> tasks = taskRepository.findByUserIdAndStatus(userId, status);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TaskDTO getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return convertToDTO(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    /**
     * Scheduled job to recalculate recommendation scores every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour
    @Transactional
    public void recalculateRecommendationScores() {
        log.info("Starting recommendation score recalculation...");

        List<Task> allActiveTasks = taskRepository.findAll().stream()
                .filter(task -> task.getStatus() != Task.Status.COMPLETED && task.getStatus() != Task.Status.CANCELLED)
                .toList();

        for (Task task : allActiveTasks) {
            double newScore = recommendationEngine.calculateRecommendationScore(task, task.getUser().getId());
            task.setRecommendationScore(newScore);
        }

        taskRepository.saveAll(allActiveTasks);
        log.info("Completed recommendation score recalculation for {} tasks", allActiveTasks.size());
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());
        dto.setDueDate(task.getDueDate());
        dto.setEstimatedDuration(task.getEstimatedDuration());
        dto.setRecommendationScore(task.getRecommendationScore());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setIsOverdue(task.isOverdue());
        dto.setTimeUntilDue(task.getTimeUntilDue());
        return dto;
    }
}
