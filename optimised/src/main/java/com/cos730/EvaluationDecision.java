package com.cos730;

public enum EvaluationDecision {
    ACCEPTED("Submission accepted."),
    REJECTED("Submission rejected."),
    REVISION_REQUIRED("Submission needs minor revisions."),
    FURTHER_REVIEW_REQUIRED("No consensus reached. Further review required."),
    WAITING_FOR_SCORES("No scores submitted yet.");

    private final String message;

    EvaluationDecision(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
