package com.marianlime.packetguard.pipeline;

import com.marianlime.packetguard.detection.ThreatDetectionService;
import com.marianlime.packetguard.metrics.PacketMetricsService;
import com.marianlime.packetguard.packet.PacketEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class PacketProcessingService {

    private static final int WORKER_COUNT = 4;
    private static final int QUEUE_CAPACITY = 10_000;

    private final BlockingQueue<PacketEvent> packetQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
    private final ExecutorService workers = Executors.newFixedThreadPool(WORKER_COUNT);

    private final ThreatDetectionService threatDetectionService;
    private final PacketMetricsService metricsService;

    private volatile boolean running = true;

    public PacketProcessingService(
            ThreatDetectionService threatDetectionService,
            PacketMetricsService metricsService
    ) {
        this.threatDetectionService = threatDetectionService;
        this.metricsService = metricsService;
    }

    @PostConstruct
    public void startWorkers() {
        for (int i = 0; i < WORKER_COUNT; i++) {
            workers.submit(this::workerLoop);
        }
    }

    public boolean submit(PacketEvent packet) {
        boolean accepted = packetQueue.offer(packet);

        if (accepted) {
            metricsService.markAccepted();
        }

        return accepted;
    }

    public long queuedPackets() {
        return packetQueue.size();
    }

    private void workerLoop() {
        while (running || !packetQueue.isEmpty()) {
            try {
                PacketEvent packet = packetQueue.take();
                threatDetectionService.inspect(packet);
                metricsService.markProcessed();
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        workers.shutdownNow();
    }
}
