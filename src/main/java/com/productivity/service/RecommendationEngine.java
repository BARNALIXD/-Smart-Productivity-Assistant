package com.productivity.service;

import com.productivity.entity.Task;
import com.productivity.entity.TaskCompletionHistory;
import com.productivity.repository.TaskCompletionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationEngine {

    private final TaskCompletionHistoryRepository historyRepository;

    @Value("${recommendation.engine.ml.enabled:true}")
    private boolean mlEnabled;

    @Value("${recommendation.engine.min.data.points:5}")
    private int minDataPoints;

    /**
     * Calculate recommendation score for a task based on multiple factors
     */
    public double calculateRecommendationScore(Task task, Long userId) {
        double score = 0.0;

        // Factor 1: Priority-based scoring (0-30 points)
        score += getPriorityScore(task.getPriority());

        // Factor 2: Due date urgency (0-40 points)
        score += getDueDateScore(task.getDueDate());

        // Factor 3: Estimated duration (0-15 points)
        score += getDurationScore(task.getEstimatedDuration());

        // Factor 4: ML-based historical patterns (0-15 points)
        if (mlEnabled) {
            score += getMlBasedScore(task, userId);
        }

        return Math.min(100.0, score);
    }

    /**
     * Priority scoring: URGENT=30, HIGH=20, MEDIUM=10, LOW=5
     */
    private double getPriorityScore(Task.Priority priority) {
        return switch (priority) {
            case URGENT -> 30.0;
            case HIGH -> 20.0;
            case MEDIUM -> 10.0;
            case LOW -> 5.0;
        };
    }

    /**
     * Due date scoring based on urgency
     */
    private double getDueDateScore(LocalDateTime dueDate) {
        if (dueDate == null) {
            return 5.0; // Low priority for tasks without due date
        }

        long hoursUntilDue = java.time.Duration.between(LocalDateTime.now(), dueDate).toHours();

        if (hoursUntilDue < 0) {
            return 40.0; // Overdue - highest urgency
        } else if (hoursUntilDue <= 4) {
            return 35.0; // Due within 4 hours
        } else if (hoursUntilDue <= 24) {
            return 30.0; // Due today
        } else if (hoursUntilDue <= 48) {
            return 20.0; // Due tomorrow
        } else if (hoursUntilDue <= 168) {
            return 15.0; // Due this week
        } else {
            return 10.0; // Due later
        }
    }

    /**
     * Duration scoring - prefer shorter tasks for momentum
     */
    private double getDurationScore(Integer estimatedDuration) {
        if (estimatedDuration == null) {
            return 5.0;
        }

        if (estimatedDuration <= 15) {
            return 15.0; // Quick wins
        } else if (estimatedDuration <= 30) {
            return 12.0;
        } else if (estimatedDuration <= 60) {
            return 8.0;
        } else {
            return 5.0;
        }
    }

    /**
     * ML-based scoring using historical completion patterns
     */
    private double getMlBasedScore(Task task, Long userId) {
        try {
            long historyCount = historyRepository.countByUserId(userId);

            if (historyCount < minDataPoints) {
                log.debug("Insufficient data for ML scoring. Count: {}, Required: {}", historyCount, minDataPoints);
                return 0.0;
            }

            double score = 0.0;

            // Pattern 1: Time of day preference (0-5 points)
            score += getTimeOfDayScore(userId);

            // Pattern 2: Day of week preference (0-5 points)
            score += getDayOfWeekScore(userId);

            // Pattern 3: Priority completion success rate (0-5 points)
            score += getPrioritySuccessScore(task, userId);

            return score;
        } catch (Exception e) {
            log.error("Error calculating ML score", e);
            return 0.0;
        }
    }

    /**
     * Score based on user's productive hours
     */
    private double getTimeOfDayScore(Long userId) {
        List<Object[]> hourlyPattern = historyRepository.getCompletionPatternByHour(userId);

        if (hourlyPattern.isEmpty()) {
            return 0.0;
        }

        int currentHour = LocalDateTime.now().getHour();
        Map<Integer, Long> hourMap = new HashMap<>();

        for (Object[] row : hourlyPattern) {
            Integer hour = (Integer) row[0];
            Long count = (Long) row[1];
            hourMap.put(hour, count);
        }

        long currentHourCompletions = hourMap.getOrDefault(currentHour, 0L);
        long maxCompletions = hourMap.values().stream().max(Long::compareTo).orElse(1L);

        return (currentHourCompletions / (double) maxCompletions) * 5.0;
    }

    /**
     * Score based on user's productive days
     */
    private double getDayOfWeekScore(Long userId) {
        List<Object[]> dailyPattern = historyRepository.getCompletionPatternByDayOfWeek(userId);

        if (dailyPattern.isEmpty()) {
            return 0.0;
        }

        DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();
        Map<DayOfWeek, Long> dayMap = new HashMap<>();

        for (Object[] row : dailyPattern) {
            DayOfWeek day = (DayOfWeek) row[0];
            Long count = (Long) row[1];
            dayMap.put(day, count);
        }

        long currentDayCompletions = dayMap.getOrDefault(currentDay, 0L);
        long maxCompletions = dayMap.values().stream().max(Long::compareTo).orElse(1L);

        return (currentDayCompletions / (double) maxCompletions) * 5.0;
    }

    /**
     * Score based on historical success with similar priority tasks
     */
    private double getPrioritySuccessScore(Task task, Long userId) {
        if (task.getPriority() == null) {
            return 0.0;
        }

        Double avgDuration = historyRepository.getAverageDurationByPriority(userId, task.getPriority());

        if (avgDuration == null || task.getEstimatedDuration() == null) {
            return 0.0;
        }

        // Reward tasks that match historical completion times
        double ratio = task.getEstimatedDuration() / avgDuration;

        if (ratio >= 0.8 && ratio <= 1.2) {
            return 5.0; // Similar to past successful tasks
        } else if (ratio < 0.8) {
            return 3.0; // Shorter than usual
        } else {
            return 1.0; // Longer than usual
        }
    }

    /**
     * Get recommended tasks for the user
     */
    public String getRecommendationExplanation(Task task, Long userId) {
        StringBuilder explanation = new StringBuilder();

        explanation.append("Recommendation factors: ");

        // Priority
        explanation.append(String.format("Priority (%s), ", task.getPriority()));

        // Due date
        if (task.getDueDate() != null) {
            if (task.isOverdue()) {
                explanation.append("OVERDUE, ");
            } else {
                Long hoursUntilDue = task.getTimeUntilDue() / 60;
                explanation.append(String.format("Due in %d hours, ", hoursUntilDue));
            }
        }

        // Duration
        if (task.getEstimatedDuration() != null) {
            explanation.append(String.format("Est. %d min, ", task.getEstimatedDuration()));
        }

        // ML insights
        if (mlEnabled && historyRepository.countByUserId(userId) >= minDataPoints) {
            explanation.append("Based on your completion patterns");
        }

        return explanation.toString();
    }
}
