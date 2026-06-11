package com.marianlime.packetguard.packet;

import com.marianlime.packetguard.pipeline.PacketProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/packets")
public class PacketController {

    private final PacketProcessingService processingService;

    public PacketController(PacketProcessingService processingService) {
        this.processingService = processingService;
    }

    @PostMapping
    public PacketIngestResponse ingest(@RequestBody PacketEvent packet) {
        boolean accepted = processingService.submit(packet);

        if (!accepted) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Packet queue is full");
        }

        return new PacketIngestResponse(true, processingService.queuedPackets());
    }
}
