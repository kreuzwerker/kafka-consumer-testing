package de.kreuzwerker.blogs.kafkaconsumertesting;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.springframework.kafka.test.utils.KafkaTestUtils.producerProps;

@TestConfiguration
public class KafkaConfiguration {

  private static KafkaAvroSerializer kafkaAvroSerializer() {
    var config =
        Map.of(
            KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
            "mock://testUrl",
            KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS,
            true);

    return new KafkaAvroSerializer(null, config);
  }

  @Bean(name = "testKafkaTemplate")
  public KafkaTemplate<String, Object> kafkaTemplate(
      @Value("${spring.embedded.kafka.brokers}") String bootstrapServers) {
    var producerProperties = producerProps(bootstrapServers);
    producerProperties.put(
        AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
    producerProperties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

    var stringSerializer = new StringSerializer();
    var valueSerializer = kafkaAvroSerializer();
    var producerFactory =
        new DefaultKafkaProducerFactory<>(producerProperties, stringSerializer, valueSerializer);
    return new KafkaTemplate<>(producerFactory);
  }
}
