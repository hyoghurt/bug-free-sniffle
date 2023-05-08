package org.example.tracker.service;

import org.example.tracker.dao.PingRepository;
import org.example.tracker.dao.entity.PongEntity;
import org.example.tracker.dto.Pong;
import org.example.tracker.security.UserDetailsManager;

public class PingService {
    private PingRepository repository = new PingRepository();
    private UserDetailsManager manager = new UserDetailsManager();

    public Pong ping() {
        System.out.println(manager.ping());
        PongEntity entity = repository.ping();
        return new Pong(entity.getPong());
    }
}