package edu.microservices.notificationservice;

import edu.microservices.notificationservice.event.OrderPlacedEvent;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "notificationTopic")
    public void handleNotification(@Nonnull final OrderPlacedEvent orderPlacedEvent) {
        /* send out an email notification */
        log.info("Received Notification for Order - {}", orderPlacedEvent.getOrderNumber());
    }
}