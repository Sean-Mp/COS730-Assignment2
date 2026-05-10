package com.cos730;

import java.util.List;

public class EvaluationService {
    private final DatabaseManager databaseManager;
    private final EvaluationStrategy evaluationStrategy;
    private final EventBus eventBus;

    public EvaluationService(
        DatabaseManager databaseManager,
        EvaluationStrategy evaluationStrategy,
        EventBus eventBus
    ) {
        this.databaseManager = databaseManager;
        this.evaluationStrategy = evaluationStrategy;
        this.eventBus = eventBus;
    }

    public EvaluationDecision submitScore(String reviewerName, int score, String submissionTitle) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100.");
        }

        if (!databaseManager.submissionExists(submissionTitle)) {
            throw new IllegalArgumentException("No submission found with title: " + submissionTitle);
        }

        if (!databaseManager.isReviewerAssignedToSubmission(reviewerName, submissionTitle)) {
            throw new IllegalArgumentException(reviewerName + " is not assigned to this submission.");
        }

        if (databaseManager.hasReviewerSubmittedScore(reviewerName, submissionTitle)) {
            throw new IllegalArgumentException(reviewerName + " has already submitted a score for this submission.");
        }

        databaseManager.saveEvaluationScore(reviewerName, score, submissionTitle);
        return evaluateSubmission(submissionTitle);
    }

    public EvaluationDecision evaluateSubmission(String submissionTitle) {
        List<Integer> scores = databaseManager.fetchScoresForSubmission(submissionTitle);
        EvaluationDecision decision = evaluationStrategy.evaluate(scores);
        eventBus.publishDecision(decision);
        return decision;
    }
}
