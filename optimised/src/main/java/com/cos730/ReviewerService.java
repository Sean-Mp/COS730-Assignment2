package com.cos730;

import java.util.List;

public class ReviewerService {
    private final DatabaseManager databaseManager;

    public ReviewerService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Reviewer> getEligibleReviewers() {
        return databaseManager.getEligibleReviewers();
    }

    public void assignReviewers(SubmissionData submission) {
        List<Reviewer> eligibleReviewers = getEligibleReviewers();
        for (Reviewer reviewer : eligibleReviewers) {
            reviewer.requestReview(submission);
            databaseManager.assignReviewerToSubmission(reviewer, submission.getTitle());
        }
    }
}
