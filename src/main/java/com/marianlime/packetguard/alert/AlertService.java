package com.marianlime.packetguard.alert;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class AlertService {

    private static final int MAX_ALERTS = 200;

    private final Deque<SecurityAlert> alerts = new ConcurrentLinkedDeque<>();

    public void record(SecurityAlert alert) {
        alerts.addFirst(alert);

        while (alerts.size() > MAX_ALERTS) {
            alerts.pollLast();
        }
    }

    public List<SecurityAlert> recentAlerts() {
        return new ArrayList<>(alerts);
    }
}
