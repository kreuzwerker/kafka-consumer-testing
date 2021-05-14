package de.kreuzwerker.blogs.kafkaconsumertesting.user;

import de.kreuzwerker.blogs.kafkaconsumertesting.PostgresAndKafkaTestCase;
import de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent;
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
class UserMessageConsumerTest extends PostgresAndKafkaTestCase {

  @Autowired UserMessageConsumer testConsumer;
  @Autowired UserService userService;
  @Autowired UserRepository userRepository;

  private static final String DEFAULT_FIRST_NAME = "Laura";
  private static final String DEFAULT_LAST_NAME = "Craft";

  private static final String EVENT_TYPE_KEY = "eventType";
  private static final String EVENT_UPDATE = "UPDATE";
  private static final String EVENT_DELETE = "DELETE";
  private static final String TOPIC = "staging-users";

  private UserEntity existingUser;
  private final String existingUserId = UUID.randomUUID().toString();

  KafkaConsumer consumer;
  KafkaProducer<Object, Object> producer;

  @Test
  void testEventCreatingUserInRepo() throws ExecutionException, InterruptedException {
    assumeTrue(kafkaContainer.isRunning());
    String newUserId = UUID.randomUUID().toString();
    UserEvent event =
        UserEvent.newBuilder().setId(newUserId).setFirstName("Jane").setLastName("Doe").build();

    List<Header> headers = singletonList(new RecordHeader(EVENT_TYPE_KEY, EVENT_UPDATE.getBytes()));
    ProducerRecord record = new ProducerRecord<>(TOPIC, null, event.getId(), event, headers);

    sendEvent(producer, record);

    ConsumerRecord<String, UserEvent> singleRecord =
        KafkaTestUtils.getSingleRecord(consumer, TOPIC);

    testConsumer.consume(singleRecord);
    Optional<UserEntity> result = userRepository.findUserEntityByUserId(newUserId);
    assertThat(result).isPresent();
    assertThat(result.get().getFirstName()).isEqualTo("Jane");
    assertThat(result.get().getLastName()).isEqualTo("Doe");
  }

  @Test
  void testEventUpdatingExistingUser() throws ExecutionException, InterruptedException {
    assumeTrue(kafkaContainer.isRunning());
    assumeTrue(userRepository.findUserEntityByUserId(existingUserId).isPresent());

    UserEvent event =
        UserEvent.newBuilder()
            .setId(existingUserId)
            .setFirstName("Change")
            .setLastName("Happened")
            .build();

    List<Header> headers = singletonList(new RecordHeader(EVENT_TYPE_KEY, EVENT_UPDATE.getBytes()));
    ProducerRecord record = new ProducerRecord<>(TOPIC, null, event.getId(), event, headers);

    sendEvent(producer, record);

    ConsumerRecord<String, UserEvent> singleRecord =
        KafkaTestUtils.getSingleRecord(consumer, TOPIC);

    testConsumer.consume(singleRecord);
    Optional<UserEntity> result = userRepository.findUserEntityByUserId(existingUserId);
    assertThat(result.get().getFirstName()).isEqualTo("Change");
    assertThat(result.get().getLastName()).isEqualTo("Happened");
  }

  @Test
  void testEventDeletingUser() throws ExecutionException, InterruptedException {
    assumeTrue(kafkaContainer.isRunning());
    assumeTrue(userRepository.findUserEntityByUserId(existingUserId).isPresent());

    UserEvent event =
        UserEvent.newBuilder().setId(existingUserId).setFirstName(null).setLastName(null).build();

    List<Header> headers = singletonList(new RecordHeader(EVENT_TYPE_KEY, EVENT_DELETE.getBytes()));
    ProducerRecord record = new ProducerRecord<>(TOPIC, null, event.getId(), event, headers);

    sendEvent(producer, record);

    ConsumerRecord<String, UserEvent> singleRecord =
        KafkaTestUtils.getSingleRecord(consumer, TOPIC);

    testConsumer.consume(singleRecord);
    Optional<UserEntity> result = userRepository.findUserEntityByUserId(existingUserId);
    assertThat(result).isEmpty();
  }

  private void createTestDataInRepository() {
    existingUser = new UserEntity();
    existingUser.setUserId(existingUserId);
    existingUser.setFirstName(DEFAULT_FIRST_NAME);
    existingUser.setLastName(DEFAULT_LAST_NAME);
    userRepository.save(existingUser);
  }

  @BeforeEach
  void prepare() {
    consumer = createEventConsumer();
    consumer.subscribe(Collections.singleton(TOPIC));
    Duration duration = Duration.ofSeconds(5);
    consumer.poll(duration);
    producer = createProducer();
    createTestDataInRepository();
  }

  @AfterEach
  void cleanup() {
    producer.close();
    consumer.close();
    userRepository.deleteAll();
  }
}
