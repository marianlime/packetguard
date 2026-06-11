package com.marianlime.packetguard.packet;

public record PacketIngestResponse(
        boolean accepted,
        long queuedPackets
) {
}
