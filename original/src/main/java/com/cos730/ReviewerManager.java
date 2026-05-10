package com.cos730;

import java.util.List;

public class ReviewerManager {
    private final Database database;

    public ReviewerManager() {
        this.database = new Database();
    }

    public List<Reviewer> getAvailableReviewers() {
        List<Reviewer> reviewers = database.fetchReviewers();

        //Filter Conflicts
        reviewers = filterConflicts(reviewers);

        //Check workload
        reviewers = checkWorkload(reviewers);

        return reviewers;
    }

    public void assignReviewers(List<Reviewer> reviewers) {
        for (Reviewer reviewer : reviewers) {
            reviewer.assignReview();
            database.incrementReviewerWorkload(reviewer.getName());
        }
    }

    private List<Reviewer> filterConflicts(List<Reviewer> reviewers) {
        // Implementation for filtering conflicts
        return reviewers.stream()
            .filter(reviewer -> reviewer.isAvailable())
            .toList();
    }

    private List<Reviewer> checkWorkload(List<Reviewer> reviewers) {
        return reviewers.stream()
            .filter(reviewer -> reviewer.getWorkload() < 5)
            .toList();
    }
}
