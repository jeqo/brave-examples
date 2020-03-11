package brave.example.messaging;

import brave.Tracing;
import brave.example.common.ZipkinConfig;
import brave.kafka.clients.KafkaTracing;
import brave.messaging.MessagingTracing;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class KafkaProducerMain {
    public static void main(String[] args) {
        var reporter = ZipkinConfig.load().reporter();
        var tracing = Tracing.newBuilder()
                .spanReporter(reporter)
                .localServiceName("kafka-producer")
                .build();
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
