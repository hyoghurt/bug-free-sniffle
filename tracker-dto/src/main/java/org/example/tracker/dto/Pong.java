package org.example.tracker.dto;

public class Pong {
    private String pong;

    public Pong(String pong) {
        this.pong = pong;
    }

    @Override
    public String toString() {
        return pong;
    }
}