package zendesk.kafka;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

import java.util.HashMap;
import java.util.Map;

public class UserEventProducer {

  private final KafkaProducer<String, JsonObject> producer;

  public UserEventProducer(Vertx vertx) {

    Map<String, String> config = new HashMap<>();
    config.put("bootstrap.servers", "localhost:9092");
    config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    config.put("value.serializer", "io.vertx.kafka.client.serialization.JsonObjectSerializer");
    config.put("acks", "1");

    producer = KafkaProducer.create(vertx, config);
  }

  public void publish(String eventType, JsonObject payload) {

    JsonObject event = new JsonObject()
      .put("type", eventType)
      .put("payload", payload)
      .put("timestamp", System.currentTimeMillis());

    KafkaProducerRecord<String, JsonObject> record =
      KafkaProducerRecord.create("user-events", event);

    producer.send(record, ar -> {
      if (ar.succeeded()) {
        System.out.println("Event sent to Kafka: " + eventType);
      } else {
        System.err.println("Failed to send event: " + ar.cause());
      }
    });
  }
}

