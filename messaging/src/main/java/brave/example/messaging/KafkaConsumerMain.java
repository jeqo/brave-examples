package brave.example.messaging;

import brave.example.tracing.TracingConfig;
import brave.kafka.clients.KafkaTracing;
import brave.messaging.MessagingTracing;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import static java.lang.System.out;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

public class KafkaConsumerMain {

  public static void main(String[] args) {
    var config = TracingConfig.load();
    var reporter = config.reporter();
    var tracing = config.tracing("kafka-consumer", reporter);
    var msgTracing = MessagingTracing.newBuilder(tracing)
        .build();
    var kafkaTracing = KafkaTracing.newBuilder(msgTracing)
        .build();

    var consumerConfig = new Properties();
    consumerConfig.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
    consumerConfig.put(GROUP_ID_CONFIG, "kafka-consumer-main");
    var consumer = kafkaTracing.consumer(
        new KafkaConsumer<>(consumerConfig, new StringDeserializer(), new StringDeserializer()));

    consumer.subscribe(List.of("foobar"));

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      consumer.close();
      tracing.close();
      reporter.close();
    }));

    while (!Thread.interrupted()) {
      for (var record : consumer.poll(Duration.ofSeconds(1))) {
        var span = kafkaTracing.nextSpan(record).name("process").start();
        try {
          out.println(record);
        } catch (Exception e) {
          span.error(e);
        } finally {
          span.finish();
        }
      }
    }
  }
}
