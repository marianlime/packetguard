package com.marianlime.packetguard.detection;

public record DetectionSnapshot(
        int uniqueDestinationPorts,
        int synPackets,
        int totalPackets
) {
}
