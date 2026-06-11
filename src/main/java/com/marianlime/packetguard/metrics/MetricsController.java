package com.marianlime.packetguard.metrics;

import com.marianlime.packetguard.pipeline.PacketProcessingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {

    private final PacketMetricsService metricsService;
    private final PacketProcessingService processingService;

    public MetricsController(
            PacketMetricsService metricsService,
            PacketProcessingService processingService
    ) {
        this.metricsService = metricsService;
        this.processingService = processingService;
    }

    @GetMapping("/api/v1/metrics")
    public PacketMetrics metrics() {
        return metricsService.snapshot(processingService.queuedPackets());
    }
}
