package org.example.tracker.dao.repository;

import org.example.tracker.dao.entity.PongEntity;

public class PingRepository {
    public PongEntity ping() {
        return new PongEntity();
    }
}
