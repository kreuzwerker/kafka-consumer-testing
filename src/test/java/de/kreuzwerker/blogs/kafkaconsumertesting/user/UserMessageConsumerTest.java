package de.kreuzwerker.blogs.kafkaconsumertesting.user;

import de.kreuzwerker.blogs.kafkaconsumertesting.PostgresAndKafkaTestCase;
import de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@Slf4j
class UserMessageConsumerTest extends PostgresAndKafkaTestCase {

  @Autowired UserMessageConsumer testConsumer;
  @Autowired UserService userService;
  @Autowired UserRepository userRepository;

  private static final String EVENT_TYPE_KEY = "eventType";
  private static final String EVENT_CREATE = "UPDATE";
  private static final String EVENT_DELETE = "DELETE";
  private static final String TOPIC = "staging-users";

  private UserEntity testEntity;

  KafkaConsumer consumer;
  KafkaProducer<Object, Object> producer;

  @Test
  void testCreateUser() throws ExecutionException, InterruptedException {
    assumeTrue(kafkaContainer.isRunning());
    String userId = UUID.randomUUID().toString();
    UserEvent event =
        UserEvent.newBuilder().setId(userId).setFirstName("Jane").setLastName("Doe").build();

    List<Header> headers = singletonList(new RecordHeader(EVENT_TYPE_KEY, EVENT_CREATE.getBytes()));
    ProducerRecord record = new ProducerRecord<>(TOPIC, null, event.getId(), event, headers);

    sendEvent(producer, record);

    ConsumerRecord<String, UserEvent> singleRecord =
        KafkaTestUtils.getSingleRecord(consumer, TOPIC);

    testConsumer.consume(singleRecord);
    Optional<UserEntity> userEntityByUserId = userRepository.findUserEntityByUserId(userId);
    assertThat(userEntityByUserId).isPresent();
    assertThat(userEntityByUserId.get().getFirstName()).isEqualTo("Jane");
    assertThat(userEntityByUserId.get().getLastName()).isEqualTo("Doe");
  }

  @BeforeEach
  void prepare() {
    consumer = createEventConsumer();
    consumer.subscribe(Collections.singleton(TOPIC));
    Duration duration = Duration.ofSeconds(5);
    consumer.poll(duration);
    producer = createProducer();
  }

  @AfterEach
  void cleanup() {
    producer.close();
    consumer.close();
    userRepository.deleteAll();
  }
}
