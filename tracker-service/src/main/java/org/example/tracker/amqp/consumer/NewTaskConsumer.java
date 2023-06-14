package org.example.tracker.amqp.consumer;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tracker.amqp.message.NewTaskMsg;
import org.example.tracker.entity.EmployeeEntity;
import org.example.tracker.entity.TaskEntity;
import org.example.tracker.service.TaskService;
import org.example.tracker.smtp.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewTaskConsumer {
    private final EmailService emailService;
    private final TaskService taskService;

    @RabbitListener(queues = "${custom.amqp.new-task-queue}")
    public void process(NewTaskMsg msg) throws MessagingException {
        log.info("receive mail queue {}", msg);
        sendEmail(msg.getTaskId());
    }

    private void sendEmail(Integer taskId) throws MessagingException {
        TaskEntity taskEntity = taskService.getTaskEntity(taskId);
        EmployeeEntity assignees = taskEntity.getAssignees();
        if (assignees != null && assignees.getEmail() != null) {
            String subject = "Новая задача";
            String text = String.format("%s, у тебя новая задача %d. %s",
                    assignees.getFirstName(), taskEntity.getId(), taskEntity.getTitle());

            EmailService.EmailDetails details = EmailService.EmailDetails.builder()
                    .to(assignees.getEmail())
                    .subject(subject)
                    .text(text)
                    .build();

            emailService.send(details);
        }
    }
}
