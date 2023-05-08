package org.example.tracker.dao;

import org.example.tracker.dao.entity.PongEntity;

public class PingRepository {
    public PongEntity ping() {
        return new PongEntity();
    }
}