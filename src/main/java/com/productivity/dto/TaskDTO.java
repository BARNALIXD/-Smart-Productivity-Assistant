package com.productivity.dto;

import com.productivity.entity.Task;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private Task.Priority priority;
    private Task.Status status;
    private LocalDateTime dueDate;
    private Integer estimatedDuration;
    private Double recommendationScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isOverdue;
    private Long timeUntilDue;
}
