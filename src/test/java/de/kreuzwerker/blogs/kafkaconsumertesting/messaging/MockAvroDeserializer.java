package de.kreuzwerker.blogs.kafkaconsumertesting.messaging;

import de.kreuzwerker.blogs.kafkaconsumertesting.users.events.UserEvent;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;

public class MockAvroDeserializer extends KafkaAvroDeserializer {
    public static final String USERS_TOPIC = "staging-users";

    @Override
    public Object deserialize(String topic, byte[] bytes) {
        if (topic.equals(USERS_TOPIC)) {
            this.schemaRegistry = getMockClient(UserEvent.SCHEMA$);
        }

        return super.deserialize(topic, bytes);
    }

    private static SchemaRegistryClient getMockClient(final Schema schema$) {
        return new MockSchemaRegistryClient() {
            @Override
            public synchronized Schema getById(int id) {
                return schema$;
            }
        };
    }
}
