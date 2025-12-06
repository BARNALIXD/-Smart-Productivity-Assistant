package com.productivity.repository;

import com.productivity.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserIdOrderByRecommendationScoreDesc(Long userId);

    List<Task> findByUserIdAndStatus(Long userId, Task.Status status);

    List<Task> findByUserIdAndStatusNot(Long userId, Task.Status status);

    List<Task> findByUserIdAndDueDateBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Task t WHERE t.user.id = ?1 AND t.status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY t.recommendationScore DESC")
    List<Task> findActiveTasksByUserOrderByScore(Long userId);

    @Query("SELECT t FROM Task t WHERE t.user.id = ?1 AND t.dueDate < ?2 AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<Task> findOverdueTasks(Long userId, LocalDateTime now);

    @Query("SELECT t FROM Task t WHERE t.user.id = ?1 AND t.status = 'COMPLETED'")
    List<Task> findCompletedTasks(Long userId);

    long countByUserIdAndStatus(Long userId, Task.Status status);
}
