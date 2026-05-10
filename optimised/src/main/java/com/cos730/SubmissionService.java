package com.cos730;

public class SubmissionService {
    private final DatabaseManager databaseManager;
    private final ReviewerService reviewerService;

    public SubmissionService(DatabaseManager databaseManager, ReviewerService reviewerService) {
        this.databaseManager = databaseManager;
        this.reviewerService = reviewerService;
    }

    public void createSubmission(SubmissionData data) {
        databaseManager.saveSubmission(data);
        reviewerService.assignReviewers(data);
    }
}
