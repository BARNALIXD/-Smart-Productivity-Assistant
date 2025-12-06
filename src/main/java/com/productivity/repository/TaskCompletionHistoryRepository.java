package com.productivity.repository;

import com.productivity.entity.TaskCompletionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskCompletionHistoryRepository extends JpaRepository<TaskCompletionHistory, Long> {

    List<TaskCompletionHistory> findByUserIdOrderByCompletedAtDesc(Long userId);

    @Query("SELECT AVG(t.actualDuration) FROM TaskCompletionHistory t WHERE t.userId = ?1 AND t.priority = ?2")
    Double getAverageDurationByPriority(Long userId, com.productivity.entity.Task.Priority priority);

    @Query("SELECT t.dayOfWeek, COUNT(t) FROM TaskCompletionHistory t WHERE t.userId = ?1 GROUP BY t.dayOfWeek")
    List<Object[]> getCompletionPatternByDayOfWeek(Long userId);

    @Query("SELECT t.hourOfDay, COUNT(t) FROM TaskCompletionHistory t WHERE t.userId = ?1 GROUP BY t.hourOfDay")
    List<Object[]> getCompletionPatternByHour(Long userId);

    List<TaskCompletionHistory> findByUserIdAndCompletedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    long countByUserId(Long userId);
}
