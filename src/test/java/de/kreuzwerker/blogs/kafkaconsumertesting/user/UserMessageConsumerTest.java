package de.kreuzwerker.blogs.kafkaconsumertesting.user;

import de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
@EmbeddedKafka(
    brokerProperties = {"listeners=PLAINTEXT://127.0.0.1:9092", "port=9092"},
    partitions = 1)
@SpringBootTest
@ActiveProfiles("test")
class UserMessageConsumerTest {

  @Autowired UserMessageConsumer testConsumer;
  @Autowired UserService userService;
  @Autowired UserRepository userRepository;

  @Autowired EmbeddedKafkaBroker embeddedKafkaBroker;

  BlockingQueue<ConsumerRecord<String, UserEvent>> records;
  KafkaMessageListenerContainer<String, UserEvent> container;

  private static final String DEFAULT_FIRST_NAME = "Laura";
  private static final String DEFAULT_LAST_NAME = "Craft";

  private static final String EVENT_TYPE_KEY = "eventType";
  private static final String EVENT_UPDATE = "UPDATE";
  private static final String EVENT_DELETE = "DELETE";
  private static final String TOPIC = "staging-users";

  private UserEntity existingUser;
  private final String existingUserId = UUID.randomUUID().toString();

  Producer<Object, Object> producer;
  Consumer<String, UserEvent> consumer;

  @Test
  public void testEventCreatingUserInRepo() throws Exception {
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
  public void testEventUpdatingExistingUser() throws Exception {
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
  public void testEventDeletingExistingUser() throws Exception {
    assumeTrue(userRepository.findUserEntityByUserId(existingUserId).isPresent());
    UserEvent event =
        UserEvent.newBuilder()
            .setId(existingUserId)
            .setFirstName("Change")
            .setLastName("Happened")
            .build();

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

  private void createProducer() {
    Map<String, Object> producerConfigs =
        new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
    producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    producerConfigs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
    producer = new DefaultKafkaProducerFactory<>(producerConfigs).createProducer();
  }

  private void createConsumer() {
    Map<String, Object> consumerConfigs =
        new HashMap<>(KafkaTestUtils.consumerProps("kafkatest", "false", embeddedKafkaBroker));
    consumerConfigs.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
    consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerConfigs.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
    consumerConfigs.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
    consumer = new DefaultKafkaConsumerFactory<String, UserEvent>(consumerConfigs).createConsumer();
  }

  private void sendEvent(Producer producer, ProducerRecord record)
      throws ExecutionException, InterruptedException {

    Future sendFuture = producer.send(record);

    RecordMetadata metadata = (RecordMetadata) sendFuture.get();
    log.info(
        "RecordMetadata topic: {}, offset: {}, partition: {}",
        metadata.topic(),
        metadata.offset(),
        metadata.partition());
  }

  @BeforeEach
  void prepare() {
    createProducer();
    createConsumer();
    consumer.subscribe(Collections.singleton(TOPIC));
    Duration duration = Duration.ofSeconds(5);
    consumer.poll(duration);
    createTestDataInRepository();
  }

  @AfterEach
  void cleanup() {
    producer.close();
    consumer.close();
    userRepository.deleteAll();
  }
}
