package org.example.tracker.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface EmailService {
    boolean send(EmailDetails details);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class EmailDetails {
        private String to;
        private String text;
        private String subject;
    }
}
