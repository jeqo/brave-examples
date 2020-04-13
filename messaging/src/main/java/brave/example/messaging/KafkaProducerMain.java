package brave.example.messaging;

import brave.example.tracing.TracingConfig;
import brave.kafka.clients.KafkaTracing;
import brave.messaging.MessagingTracing;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

public class KafkaProducerMain {
  public static void main(String[] args) {
    var config = TracingConfig.load();
    var reporter = config.reporter();
    var tracing = config.tracing("kafka-producer", reporter);
    var msgTracing = MessagingTracing.newBuilder(tracing)
        .build();
    var kafkaTracing = KafkaTracing.newBuilder(msgTracing)
        .build();

    var producerConfig = new Properties();
    producerConfig.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");

    var producer = kafkaTracing.producer(
        new KafkaProducer<>(producerConfig, new StringSerializer(), new StringSerializer()));

    var record = new ProducerRecord<>("foobar", "foo", "bar");

    producer.send(record);

    producer.close();
    tracing.close();
    reporter.close();
  }
}
