package io.zipkin.contrib.brave.example.http;

import brave.Tracing;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Sender;
import zipkin2.reporter.okhttp3.OkHttpSender;

public class ZipkinConfig {

    final String senderType;
    final Config senderConfig;

    static ZipkinConfig load() {
        return load(ConfigFactory.load().getConfig("zipkin"));
    }

    static ZipkinConfig load(Config config) {
        var senderType = config.hasPath("senderType") ? config.getString("senderType") : "okhttp";
        var senderConfig = config.getConfig("sender");
        return new ZipkinConfig(senderType, senderConfig);
    }

    ZipkinConfig(String senderType, Config senderConfig) {
        this.senderType = senderType;
        this.senderConfig = senderConfig;
    }

    public Sender sender() {
        switch (senderType) {
            case "okhttp":
                return OkHttpSenderConfig.load(senderConfig).sender();
            default:
                throw new IllegalArgumentException("Sender unknown");
        }
    }

    public AsyncReporter<Span> reporter() {
        return AsyncReporter.create(sender());
    }

    public Tracing tracing(String localServiceName) {
        return Tracing.newBuilder()
                .localServiceName(localServiceName)
                .spanReporter(reporter())
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
}
