package com.marianlime.packetguard.alert;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/api/v1/alerts")
    public List<SecurityAlert> alerts() {
        return alertService.recentAlerts();
    }
}
