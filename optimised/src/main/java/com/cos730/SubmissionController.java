package com.cos730;

import java.util.List;

public class SubmissionController {

    private final Validator validator;
    private final ReviewerService reviewerService;
    private final SubmissionService submissionService;
    private final EvaluationService evaluationService;

    public SubmissionController() {
        Database database = new Database();
        DatabaseManager databaseManager = new DatabaseManager(database);
        NotificationService notificationService = new NotificationService();
        EventBus eventBus = new EventBus(notificationService);

        this.validator = new Validator();
        this.reviewerService = new ReviewerService(databaseManager);
        this.submissionService = new SubmissionService(databaseManager, reviewerService);
        this.evaluationService = new EvaluationService(
            databaseManager,
            new ConsensusEvaluationStrategy(),
            eventBus
        );
    }
    
    
    public String submit(String title, String researcher, String keywords, String abstractText) {
        SubmissionData data = new SubmissionData(title, researcher, keywords, abstractText);
        String validationError = validator.validateFormat(data);
        if (validationError != null) {
            return validationError;
        }

        try {
            submissionService.createSubmission(data);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }

        return null;
    }

    public List<Reviewer> getAvailableReviewers() {
        return reviewerService.getEligibleReviewers();
    }

    public String submitReviewerScore(String reviewerName, int score, String submissionTitle) {
        if (score < 0 || score > 100) {
            return "Score must be between 0 and 100.";
        }

        try {
            evaluationService.submitScore(reviewerName, score, submissionTitle);
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }
}
