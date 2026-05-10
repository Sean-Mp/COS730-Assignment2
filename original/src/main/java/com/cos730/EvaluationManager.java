package com.cos730;

import java.util.List;

public class EvaluationManager {

    private boolean evaluationStarted;
    private String activeSubmissionTitle;
    private final Database database;
    private final NotificationService notificationService;

    public EvaluationManager() {
        this.database = new Database();
        this.notificationService = new NotificationService();
    }

    public void startEvaluation(String submissionTitle) {
        evaluationStarted = true;
        activeSubmissionTitle = submissionTitle;
    }

    public String evaluateSubmission(String submissionTitle) {
        if (!evaluationStarted || !submissionTitle.equals(activeSubmissionTitle)) {
            startEvaluation(submissionTitle);
        }

        List<Integer> scores = database.fetchScoresForSubmission(submissionTitle);
        double averageScore = calculateAverage(scores);
        boolean consensusReached = checkConsensus(scores);
        String evaluationResult = applyRules(scores, averageScore, consensusReached);
        notificationService.sendNotification();
        return evaluationResult;
    }

    public void submitScore(String reviewerName, int score, String submissionTitle) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Score must be between 0 and 100.");
        }

        database.saveEvaluationScore(reviewerName, score, submissionTitle);
    }

    private double calculateAverage(List<Integer> scores){
        if (scores.isEmpty()) {
            return 0;
        }

        int total = 0;
        for(int score: scores){
            total += score;
        }
        return (double) total / scores.size();
    }

    private boolean checkConsensus(List<Integer> scores) {
        if (scores.size() < 2) {
            return false;
        }

        int minScore = scores.get(0);
        int maxScore = scores.get(0);

        for (int score : scores) {
            if (score < minScore) {
                minScore = score;
            }
            if (score > maxScore) {
                maxScore = score;
            }
        }

        return (maxScore - minScore) <= 10;
    }

    private String applyRules(List<Integer> scores, double averageScore, boolean consensusReached) {
        if (scores.isEmpty()) {
            notificationService.notifyRevision();
            return "No scores submitted yet.";
        }

        if (!consensusReached) {
            notificationService.notifyRevision();
            return "No consensus reached. Further review required.";
        }

        if (averageScore >= 75) {
            notificationService.notifyAcceptance();
            return "Submission accepted.";
        }

        if (averageScore >= 50) {
            notificationService.notifyRevision();
            return "Submission needs minor revisions.";
        }

        notificationService.notifyRejection();
        return "Submission rejected.";
    }
}
