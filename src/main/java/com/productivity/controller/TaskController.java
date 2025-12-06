package com.productivity.controller;

import com.productivity.dto.TaskDTO;
import com.productivity.entity.Task;
import com.productivity.service.RecommendationEngine;
import com.productivity.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;
    private final RecommendationEngine recommendationEngine;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @RequestParam Long userId,
            @RequestBody TaskDTO taskDTO) {
        TaskDTO created = taskService.createTask(userId, taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(@RequestParam Long userId) {
        List<TaskDTO> tasks = taskService.getAllTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<TaskDTO>> getRecommendedTasks(@RequestParam Long userId) {
        List<TaskDTO> tasks = taskService.getRecommendedTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(
            @RequestParam Long userId,
            @PathVariable String status) {
        Task.Status taskStatus = Task.Status.valueOf(status.toUpperCase());
        List<TaskDTO> tasks = taskService.getTasksByStatus(userId, taskStatus);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @RequestBody TaskDTO taskDTO) {
        TaskDTO updated = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskDTO> completeTask(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Integer> payload) {
        Integer actualDuration = payload != null ? payload.get("actualDuration") : null;
        TaskDTO completed = taskService.completeTask(id, actualDuration);
        return ResponseEntity.ok(completed);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recalculate-scores")
    public ResponseEntity<Map<String, String>> recalculateScores() {
        taskService.recalculateRecommendationScores();
        return ResponseEntity.ok(Map.of("message", "Recommendation scores recalculated"));
    }
}
