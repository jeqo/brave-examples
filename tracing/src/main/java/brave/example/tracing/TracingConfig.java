package brave.example.tracing;

import brave.Tracing;
import brave.propagation.B3Propagation;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.brave.ZipkinSpanHandler;
import zipkin2.reporter.kafka.KafkaSender;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class TracingConfig {

  final String senderType;
  final Config senderConfig;

  public static TracingConfig load() {
    return load(ConfigFactory.load().getConfig("zipkin"));
  }

  static TracingConfig load(Config config) {
    var senderType = config.hasPath("sender-type") ? config.getString("sender-type") : "okhttp";
    var senderConfig = config.getConfig("sender");
    return new TracingConfig(senderType, senderConfig);
  }

  TracingConfig(String senderType, Config senderConfig) {
    this.senderType = senderType;
    this.senderConfig = senderConfig;
  }

  public Sender sender() {
    if ("okhttp".equals(senderType)) return OkHttpSenderConfig.load(senderConfig).sender();
    if ("kafka".equals(senderType)) return KafkaSenderConfig.load(senderConfig).sender();
    throw new IllegalArgumentException("Sender unknown");
  }

  public AsyncReporter<Span> reporter() {
    return AsyncReporter.create(sender());
  }

  public Tracing tracing(String localServiceName, Reporter<Span> spanReporter) {
    return Tracing.newBuilder()
        .localServiceName(localServiceName)
        .propagationFactory(B3Propagation.FACTORY)
        .addSpanHandler(ZipkinSpanHandler.create(spanReporter))
        .build();
  }

  static class OkHttpSenderConfig {
    final String endpoint;

    OkHttpSenderConfig(String endpoint) {
      this.endpoint = endpoint;
    }

    static OkHttpSenderConfig load(Config senderConfig) {
      var endpoint = senderConfig.getString("endpoint");
      return new OkHttpSenderConfig(endpoint);
    }

    public Sender sender() {
      return OkHttpSender.newBuilder()
          .endpoint(endpoint)
          .build();
    }
  }
  static class KafkaSenderConfig {
    final String bootstrapServers;

    KafkaSenderConfig(String bootstrapServers) {
      this.bootstrapServers = bootstrapServers;
    }

    static KafkaSenderConfig load(Config senderConfig) {
      var bootstrapServers = senderConfig.getString("bootstrapServers");
      return new KafkaSenderConfig(bootstrapServers);
    }

    public Sender sender() {
      return KafkaSender.newBuilder()
              .bootstrapServers(bootstrapServers)
              .build();
    }
  }
}
