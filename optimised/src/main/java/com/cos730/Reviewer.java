package com.cos730;

public class Reviewer {
    private String name;
    private boolean isAvailable;
    private int workload;

    public Reviewer(){
        
    }

    public Reviewer(String name, boolean isAvailable, int workload) {
        this.name = name;
        this.isAvailable = isAvailable;
        this.workload = workload;
    }

    public void requestReview(SubmissionData submission) {
        assignReview();
    }
    
    public void assignReview(){
        this.workload++;
    }


    public void submitScore(EvaluationService evaluationService, int score, String submissionTitle) {
        evaluationService.submitScore(name, score, submissionTitle);
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public int getWorkload() {
        return workload;
    }
}
