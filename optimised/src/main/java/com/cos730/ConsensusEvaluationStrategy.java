package com.cos730;

import java.util.List;

public class ConsensusEvaluationStrategy implements EvaluationStrategy {

    private static final int CONSENSUS_RANGE = 10;
    private static final int ACCEPTANCE_THRESHOLD = 75;
    private static final int REVISION_THRESHOLD = 50;

    @Override
    public EvaluationDecision evaluate(List<Integer> scores) {
        if (scores.isEmpty()) {
            return EvaluationDecision.WAITING_FOR_SCORES;
        }

        if (!hasConsensus(scores)) {
            return EvaluationDecision.FURTHER_REVIEW_REQUIRED;
        }

        double averageScore = calculateAverage(scores);
        if (averageScore >= ACCEPTANCE_THRESHOLD) {
            return EvaluationDecision.ACCEPTED;
        }

        if (averageScore >= REVISION_THRESHOLD) {
            return EvaluationDecision.REVISION_REQUIRED;
        }

        return EvaluationDecision.REJECTED;
    }

    private double calculateAverage(List<Integer> scores) {
        int total = 0;
        for (int score : scores) {
            total += score;
        }
        return (double) total / scores.size();
    }

    private boolean hasConsensus(List<Integer> scores) {
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

        return (maxScore - minScore) <= CONSENSUS_RANGE;
    }
}
