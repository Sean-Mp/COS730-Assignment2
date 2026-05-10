package com.cos730;

import java.util.List;
import java.util.Optional;

public class SubmissionController {

    private final Validator validator;
    private final ReviewerManager reviewerManager;
    private final Database database;
    private final EvaluationManager evaluationManager;

    public SubmissionController() {
        this.validator = new Validator();
        this.reviewerManager = new ReviewerManager();
        this.database = new Database();
        this.evaluationManager = new EvaluationManager();
    }
    
    
    public String submit(String title, String researcher, String keywords, String abstractText) {
        String validationError = validator.validateFormat(title, researcher, keywords, abstractText);
        if (validationError != null) {
            return validationError;
        }

        try {
            database.saveSubmission(title, researcher, keywords, abstractText);

            List<Reviewer> availableReviewers = reviewerManager.getAvailableReviewers();
            reviewerManager.assignReviewers(availableReviewers);

            evaluationManager.startEvaluation(title);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }

        return null;
    }

    public List<Reviewer> getAvailableReviewers() {
        return reviewerManager.getAvailableReviewers();
    }

    public String submitReviewerScore(String reviewerName, int score, String submissionTitle) {
        if (score < 0 || score > 100) {
            return "Score must be between 0 and 100.";
        }

        if (!database.submissionExists(submissionTitle)) {
            return "No submission found with title: " + submissionTitle;
        }

        if (database.hasReviewerSubmittedScore(reviewerName, submissionTitle)) {
            return reviewerName + " has already submitted a score for this submission.";
        }

        List<Reviewer> availableReviewers = reviewerManager.getAvailableReviewers();
        Optional<Reviewer> selectedReviewer = availableReviewers.stream()
            .filter(reviewer -> reviewer.getName().equals(reviewerName))
            .findFirst();

        if (selectedReviewer.isEmpty()) {
            return "Selected reviewer could not be found.";
        }

        try {
            selectedReviewer.get().submitScore(evaluationManager, score, submissionTitle);
            evaluationManager.evaluateSubmission(submissionTitle);
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return e.getMessage();
        }
    }
}
