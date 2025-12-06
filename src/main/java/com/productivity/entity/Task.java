package com.productivity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.TODO;

    private LocalDateTime dueDate;

    private Integer estimatedDuration; // in minutes

    private LocalDateTime completedAt;

    private Integer actualDuration; // in minutes

    @Column(nullable = false)
    private Double recommendationScore = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public enum Status {
        TODO, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public boolean isOverdue() {
        return dueDate != null &&
               dueDate.isBefore(LocalDateTime.now()) &&
               status != Status.COMPLETED &&
               status != Status.CANCELLED;
    }

    public Long getTimeUntilDue() {
        if (dueDate == null) return null;
        return java.time.Duration.between(LocalDateTime.now(), dueDate).toMinutes();
    }
}
