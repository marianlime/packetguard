package com.marianlime.packetguard.metrics;

public record PacketMetrics(
        long acceptedPackets,
        long processedPackets,
        long queuedPackets
) {
}
