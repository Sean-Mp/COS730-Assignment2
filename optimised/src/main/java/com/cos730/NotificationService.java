package com.cos730;

import javax.swing.JOptionPane;

public class NotificationService {

    private String notificationMessage;
    private String notificationTitle;
    private int notificationType;

    public void notifyAcceptance() {
        notificationMessage = "The research submission has been accepted.";
        notificationTitle = "Submission Accepted";
        notificationType = JOptionPane.INFORMATION_MESSAGE;
    }

    public void notifyRejection() {
        notificationMessage = "The research submission has been rejected.";
        notificationTitle = "Submission Rejected";
        notificationType = JOptionPane.ERROR_MESSAGE;
    }

    public void notifyRevision() {
        notificationMessage = "The research submission requires revisions or further review.";
        notificationTitle = "Submission Revision Required";
        notificationType = JOptionPane.WARNING_MESSAGE;
    }

    public void handle(EvaluationDecision decision) {
        switch (decision) {
            case ACCEPTED:
                notifyAcceptance();
                break;
            case REJECTED:
                notifyRejection();
                break;
            case REVISION_REQUIRED:
                notifyRevision();
                break;
            case FURTHER_REVIEW_REQUIRED:
            case WAITING_FOR_SCORES:
                notifyRevision();
                break;
            default:
                throw new IllegalArgumentException("Unsupported decision: " + decision);
        }

        sendNotification();
    }

    public void sendNotification() {
        if (notificationMessage == null || notificationMessage.isBlank()) {
            return;
        }

        JOptionPane.showMessageDialog(
            null,
            notificationMessage,
            notificationTitle,
            notificationType
        );
    }
}
