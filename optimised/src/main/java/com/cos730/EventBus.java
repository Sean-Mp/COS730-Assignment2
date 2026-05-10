package com.cos730;

public class EventBus {
    private final NotificationService notificationService;

    public EventBus(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void publishDecision(EvaluationDecision decision) {
        notificationService.handle(decision);
    }
}
