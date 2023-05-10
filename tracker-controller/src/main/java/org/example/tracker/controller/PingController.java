package org.example.tracker.controller;

import org.example.tracker.dto.Pong;
import org.example.tracker.service.PingService;

public class PingController {
    private PingService service = new PingService();

    public Pong ping() {
        return service.ping();
    }
}