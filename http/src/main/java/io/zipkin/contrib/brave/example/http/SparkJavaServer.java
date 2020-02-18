package io.zipkin.contrib.brave.example.http;

import brave.http.HttpTracing;
import brave.sparkjava.SparkTracing;

import static spark.Spark.*;

public class SparkJavaServer {
    public static void main(String[] args) {
        var tracing = ZipkinConfig.load().tracing("sparkjava-server");
        var httpTracing = HttpTracing.newBuilder(tracing).build();
        var sparkTracing = SparkTracing.create(httpTracing);

        before(sparkTracing.before());
        exception(Exception.class, sparkTracing.exception((e, request, response) -> e.printStackTrace()));
        afterAfter(sparkTracing.afterAfter());

        get("/foo", (req, res) -> "bar");
    }
}
