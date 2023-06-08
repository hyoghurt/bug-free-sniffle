package org.example.tracker.smtp;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface EmailService {
    void send(EmailDetails details) throws MessagingException;

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
