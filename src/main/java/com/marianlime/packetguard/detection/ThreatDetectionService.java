package com.marianlime.packetguard.detection;

import com.marianlime.packetguard.alert.AlertService;
import com.marianlime.packetguard.alert.SecurityAlert;
import com.marianlime.packetguard.packet.PacketEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ThreatDetectionService {

    private static final long WINDOW_MILLIS = 60_000;
    private static final int PORT_SCAN_THRESHOLD = 20;
    private static final int SYN_FLOOD_THRESHOLD = 50;

    private final Map<String, SourceActivityWindow> sourceWindows = new ConcurrentHashMap<>();
    private final AlertService alertService;

    public ThreatDetectionService(AlertService alertService) {
        this.alertService = alertService;
    }

    public void inspect(PacketEvent packet) {
        long nowMillis = packet.timestampEpochMillis() > 0
                ? packet.timestampEpochMillis()
                : System.currentTimeMillis();

        SourceActivityWindow window = sourceWindows.computeIfAbsent(
                packet.sourceIp(),
                ignored -> new SourceActivityWindow()
        );

        DetectionSnapshot snapshot = window.addAndSnapshot(
                packet.destinationPort(),
                packet.flags(),
                nowMillis,
                WINDOW_MILLIS
        );

        if (snapshot.uniqueDestinationPorts() == PORT_SCAN_THRESHOLD) {
            alertService.record(new SecurityAlert(
                    "PORT_SCAN",
                    packet.sourceIp(),
                    "HIGH",
                    "Source contacted many unique destination ports within a 60-second window",
                    List.of(
                            "uniqueDestinationPorts=" + snapshot.uniqueDestinationPorts(),
                            "threshold=" + PORT_SCAN_THRESHOLD,
                            "destinationIp=" + packet.destinationIp()
                    ),
                    Instant.now()
            ));
        }

        if (snapshot.synPackets() == SYN_FLOOD_THRESHOLD) {
            alertService.record(new SecurityAlert(
                    "SYN_FLOOD",
                    packet.sourceIp(),
                    "HIGH",
                    "Source generated a high number of SYN packets within a 60-second window",
                    List.of(
                            "synPackets=" + snapshot.synPackets(),
                            "threshold=" + SYN_FLOOD_THRESHOLD,
                            "destinationIp=" + packet.destinationIp()
                    ),
                    Instant.now()
            ));
        }
    }
}
