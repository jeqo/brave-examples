package brave.example.http;

import brave.example.tracing.TracingConfig;
import brave.http.HttpTracing;
import brave.sparkjava.SparkTracing;

import static spark.Spark.afterAfter;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;

public class SparkJavaServer {
  public static void main(String[] args) {
    var config = TracingConfig.load();
    var reporter = config.reporter();
    var tracing = config.tracing("sparkjava-server", reporter);
    var httpTracing = HttpTracing.newBuilder(tracing).build();
    var sparkTracing = SparkTracing.create(httpTracing);

    before(sparkTracing.before());
    exception(Exception.class,
        sparkTracing.exception((e, request, response) -> e.printStackTrace()));
    afterAfter(sparkTracing.afterAfter());

    get("/foo", (req, res) -> "bar");
  }
}
