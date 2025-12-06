package com.productivity.service;

import com.productivity.entity.Task;
import com.productivity.repository.TaskCompletionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationEngineTest {

    @Mock
    private TaskCompletionHistoryRepository historyRepository;

    @InjectMocks
    private RecommendationEngine recommendationEngine;

    private Task testTask;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
    }

    @Test
    void testCalculateScore_UrgentPriority_HighScore() {
        testTask.setPriority(Task.Priority.URGENT);
        testTask.setDueDate(LocalDateTime.now().plusHours(2));
        testTask.setEstimatedDuration(30);

        when(historyRepository.countByUserId(userId)).thenReturn(0L);

        double score = recommendationEngine.calculateRecommendationScore(testTask, userId);

        assertTrue(score > 60, "Urgent task with near due date should have high score");
    }

    @Test
    void testCalculateScore_LowPriority_LowScore() {
        testTask.setPriority(Task.Priority.LOW);
        testTask.setDueDate(LocalDateTime.now().plusDays(7));
        testTask.setEstimatedDuration(120);

        when(historyRepository.countByUserId(userId)).thenReturn(0L);

        double score = recommendationEngine.calculateRecommendationScore(testTask, userId);

        assertTrue(score < 30, "Low priority task with far due date should have low score");
    }

    @Test
    void testCalculateScore_OverdueTask_MaximumUrgency() {
        testTask.setPriority(Task.Priority.MEDIUM);
        testTask.setDueDate(LocalDateTime.now().minusHours(2)); // Overdue
        testTask.setEstimatedDuration(30);

        when(historyRepository.countByUserId(userId)).thenReturn(0L);

        double score = recommendationEngine.calculateRecommendationScore(testTask, userId);

        assertTrue(score > 50, "Overdue task should have high urgency score");
    }

    @Test
    void testCalculateScore_QuickWin_BonusPoints() {
        testTask.setPriority(Task.Priority.MEDIUM);
        testTask.setDueDate(LocalDateTime.now().plusHours(4));
        testTask.setEstimatedDuration(15); // Quick task

        when(historyRepository.countByUserId(userId)).thenReturn(0L);

        double score = recommendationEngine.calculateRecommendationScore(testTask, userId);

        assertTrue(score > 30, "Quick win task should get bonus points");
    }

    @Test
    void testCalculateScore_NoDueDate_DefaultScore() {
        testTask.setPriority(Task.Priority.MEDIUM);
        testTask.setDueDate(null);
        testTask.setEstimatedDuration(60);

        when(historyRepository.countByUserId(userId)).thenReturn(0L);

        double score = recommendationEngine.calculateRecommendationScore(testTask, userId);

        assertTrue(score >= 0 && score <= 100, "Score should be within valid range");
    }

    @Test
    void testCalculateScore_ScoreNotExceed100() {
        testTask.setPriority(Task.Priority.URGENT);
        testTask.setDueDate(LocalDateTime.now().minusHours(5)); // Very overdue
        testTask.setEstimatedDuration(10); // Very quick

        when(historyRepository.countByUserId(userId)).thenReturn(0L);

        double score = recommendationEngine.calculateRecommendationScore(testTask, userId);

        assertTrue(score <= 100.0, "Score should never exceed 100");
    }

    @Test
    void testGetRecommendationExplanation_ContainsRelevantInfo() {
        testTask.setPriority(Task.Priority.HIGH);
        testTask.setDueDate(LocalDateTime.now().plusHours(3));
        testTask.setEstimatedDuration(45);

        when(historyRepository.countByUserId(userId)).thenReturn(0L);

        String explanation = recommendationEngine.getRecommendationExplanation(testTask, userId);

        assertNotNull(explanation);
        assertTrue(explanation.contains("HIGH"));
        assertTrue(explanation.contains("min"));
    }

    @Test
    void testGetRecommendationExplanation_OverdueTask() {
        testTask.setPriority(Task.Priority.MEDIUM);
        testTask.setDueDate(LocalDateTime.now().minusHours(1));
        testTask.setEstimatedDuration(30);

        when(historyRepository.countByUserId(userId)).thenReturn(0L);

        String explanation = recommendationEngine.getRecommendationExplanation(testTask, userId);

        assertTrue(explanation.contains("OVERDUE"));
    }

    @Test
    void testCalculateScore_WithMLEnabled() {
        testTask.setPriority(Task.Priority.MEDIUM);
        testTask.setDueDate(LocalDateTime.now().plusHours(4));
        testTask.setEstimatedDuration(30);

        // Mock sufficient history for ML
        when(historyRepository.countByUserId(userId)).thenReturn(10L);
        when(historyRepository.getAverageDurationByPriority(userId, Task.Priority.MEDIUM))
                .thenReturn(35.0);
        when(historyRepository.getCompletionPatternByHour(userId)).thenReturn(java.util.Collections.emptyList());
        when(historyRepository.getCompletionPatternByDayOfWeek(userId)).thenReturn(java.util.Collections.emptyList());

        double score = recommendationEngine.calculateRecommendationScore(testTask, userId);

        assertTrue(score >= 0 && score <= 100);
        verify(historyRepository, times(1)).countByUserId(userId);
    }

    @Test
    void testCalculateScore_InsufficientHistoryForML() {
        testTask.setPriority(Task.Priority.MEDIUM);
        testTask.setDueDate(LocalDateTime.now().plusHours(4));
        testTask.setEstimatedDuration(30);

        when(historyRepository.countByUserId(userId)).thenReturn(2L); // Less than minimum

        double score = recommendationEngine.calculateRecommendationScore(testTask, userId);

        assertTrue(score >= 0 && score <= 100);
        verify(historyRepository, times(1)).countByUserId(userId);
        // ML methods should not be called
        verify(historyRepository, never()).getAverageDurationByPriority(any(), any());
    }
}
