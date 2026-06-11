package com.marianlime.packetguard.metrics;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class PacketMetricsService {

    private final AtomicLong acceptedPackets = new AtomicLong();
    private final AtomicLong processedPackets = new AtomicLong();

    public void markAccepted() {
        acceptedPackets.incrementAndGet();
    }

    public void markProcessed() {
        processedPackets.incrementAndGet();
    }

    public PacketMetrics snapshot(long queuedPackets) {
        return new PacketMetrics(
                acceptedPackets.get(),
                processedPackets.get(),
                queuedPackets
        );
    }
}
