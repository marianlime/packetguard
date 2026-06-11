package com.marianlime.packetguard.packet;

public record PacketEvent(
        String sourceIp,
        String destinationIp,
        int sourcePort,
        int destinationPort,
        String protocol,
        String flags,
        long timestampEpochMillis
) {
}
