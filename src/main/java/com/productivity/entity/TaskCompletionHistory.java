package com.productivity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_completion_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCompletionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long taskId;

    private String taskTitle;

    @Enumerated(EnumType.STRING)
    private Task.Priority priority;

    private Integer estimatedDuration;

    private Integer actualDuration;

    @Column(nullable = false)
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private Integer hourOfDay;

    private Boolean wasOverdue;

    private Long timeBeforeDue; // in minutes

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
