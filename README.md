# kafka-consumer-testing

Let's assume you have a Spring Boot microservice that listens to incoming events from Kafka, transforms them into your own business objects, writes them into a PostgreSQL database and provides them via REST interface to your frontend. The overall infrastructure provides Avro messages and the Confluent schema registry.

This sample project uses Apache Maven, with the avro-maven-plugin to download the schema files and generate the sources, but of course there are plugins for Gradle too.

Now you want to test that your Kafka consumer reads the events, transforms them into your database entities, and saves them.

When you check the internet for testing in the context of Spring Boot Kafka consumer and Avro schema, you find quite a few variants. From using the MockSchemaRegistryClient to writing your own custom Avro de-/serializers, to setting up a Testcontainers ecosystem with a Kafka, a Zookeeper and a Confluent Schema Registry, or to using the EmbeddedKafka provided by Spring in the spring-kafka-test dependency.

In this case, we decided against the use of the EmbeddedKafka and against the full Testcontainers setup and went for a setup with a smaller number of Testcontainers.

More details can be found in this blogpost: https://kreuzwerker.de/post/testing-a-kafka-consumer-with-avro-schema-messages-in-your-spring-boot
