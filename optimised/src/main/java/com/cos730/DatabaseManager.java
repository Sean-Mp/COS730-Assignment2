package com.cos730;

import java.util.List;
import java.util.stream.Collectors;

public class DatabaseManager {
    private final Database database;

    public DatabaseManager(Database database) {
        this.database = database;
    }

    public void saveSubmission(SubmissionData data) {
        database.saveSubmission(data.getTitle(), data.getResearcher(), data.getKeywords(), data.getAbstractText());
    }

    public List<Reviewer> getEligibleReviewers() {
        return database.fetchReviewers().stream()
            .filter(Reviewer::isAvailable)
            .filter(reviewer -> reviewer.getWorkload() < 5)
            .collect(Collectors.toList());
    }

    public void assignReviewerToSubmission(Reviewer reviewer, String submissionTitle) {
        database.saveReviewerAssignment(reviewer.getName(), submissionTitle);
        database.incrementReviewerWorkload(reviewer.getName());
    }

    public boolean submissionExists(String submissionTitle) {
        return database.submissionExists(submissionTitle);
    }

    public boolean hasReviewerSubmittedScore(String reviewerName, String submissionTitle) {
        return database.hasReviewerSubmittedScore(reviewerName, submissionTitle);
    }

    public boolean isReviewerAssignedToSubmission(String reviewerName, String submissionTitle) {
        return database.isReviewerAssignedToSubmission(reviewerName, submissionTitle);
    }

    public void saveEvaluationScore(String reviewerName, int score, String submissionTitle) {
        database.saveEvaluationScore(reviewerName, score, submissionTitle);
    }

    public List<Integer> fetchScoresForSubmission(String submissionTitle) {
        return database.fetchScoresForSubmission(submissionTitle);
    }
}
