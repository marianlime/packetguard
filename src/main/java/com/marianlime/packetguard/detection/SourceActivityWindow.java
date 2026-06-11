package com.marianlime.packetguard.detection;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class SourceActivityWindow {

    private final ArrayDeque<PacketObservation> observations = new ArrayDeque<>();

    public synchronized DetectionSnapshot addAndSnapshot(
            int destinationPort,
            String flags,
            long nowMillis,
            long windowMillis
    ) {
        long cutoff = nowMillis - windowMillis;

        while (!observations.isEmpty() && observations.peekFirst().timestampMillis() < cutoff) {
            observations.pollFirst();
        }

        observations.addLast(new PacketObservation(destinationPort, flags, nowMillis));

        Set<Integer> uniqueDestinationPorts = new HashSet<>();
        int synCount = 0;

        for (PacketObservation observation : observations) {
            uniqueDestinationPorts.add(observation.destinationPort());

            if (observation.flags() != null && observation.flags().toUpperCase().contains("SYN")) {
                synCount++;
            }
        }

        return new DetectionSnapshot(
                uniqueDestinationPorts.size(),
                synCount,
                observations.size()
        );
    }

    private record PacketObservation(
            int destinationPort,
            String flags,
            long timestampMillis
    ) {
    }
}
