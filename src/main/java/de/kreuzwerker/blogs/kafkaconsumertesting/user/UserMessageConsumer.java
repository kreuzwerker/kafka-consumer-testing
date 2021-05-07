package de.kreuzwerker.blogs.kafkaconsumertesting.user;

import de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Iterator;
import java.util.Optional;

@Slf4j
public class UserMessageConsumer {

    @Autowired
    private UserService userService;

    @KafkaListener(topics="staging-users", clientIdPrefix = "kafkatest")
    public void consume(ConsumerRecord<String, UserEvent> record) {
        UserEvent event = record.value();
        Optional<String> typeOpt = getEventTypeFromHeader(record.headers());
        if (typeOpt.isEmpty()) {
            // ignore
        } else {
            log.info(
                "UserMessageConsumer >> consume >> received event id {} with {}", event.getId(), typeOpt.get());
            userService.updateDb(event);
        }
    }

    private Optional<String> getEventTypeFromHeader(Headers headers) {
        Iterable<Header> itHeader = headers.headers("eventType");
        Iterator<Header> iterator = itHeader.iterator();
        if (iterator.hasNext()) {
            Header next = iterator.next();
            return Optional.of(new String(next.value()));
        }
        return Optional.empty();
    }
}
