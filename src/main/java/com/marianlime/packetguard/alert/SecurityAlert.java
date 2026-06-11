package com.marianlime.packetguard.alert;

import java.time.Instant;
import java.util.List;

public record SecurityAlert(
        String alertType,
        String sourceIp,
        String severity,
        String description,
        List<String> evidence,
        Instant createdAt
) {
}
