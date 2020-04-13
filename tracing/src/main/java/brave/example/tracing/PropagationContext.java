package brave.example.tracing;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.propagation.aws.AWSPropagation;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import zipkin2.propagation.stackdriver.StackdriverTracePropagation;

class PropagationContext {
  @Test void printB3PropagationContext() {
    // Given
    var tracing = Tracing.newBuilder()
        .propagationFactory(B3Propagation.FACTORY) // B3 Propagation
        .build();
    //   and map injector
    var injector = tracing.propagation().<Map<String, String>>injector(Map::put);
    // When
    var span = tracing.tracer().newTrace().start();
    span.finish(); // span context available
    // Then
    //  injected code has B3 propagation format.
    var carrier = new LinkedHashMap<String, String>();
    injector.inject(span.context(), carrier);
    System.out.println("B3: "+carrier);
  }

  @Test void printAwsPropagationContext() {
    // Given
    var tracing = Tracing.newBuilder()
        .propagationFactory(AWSPropagation.FACTORY) // Aws Propagation
        .build();
    //   and map injector
    var injector = tracing.propagation().<Map<String, String>>injector(Map::put);
    // When
    var span = tracing.tracer().newTrace().start();
    span.finish(); // span context available
    // Then
    //  injected code has B3 propagation format.
    var carrier = new LinkedHashMap<String, String>();
    injector.inject(span.context(), carrier);
    System.out.println("AWS: "+carrier);
  }

  @Test void printGcpPropagationContext() {
    // Given
    var tracing = Tracing.newBuilder()
        .propagationFactory(StackdriverTracePropagation.FACTORY) // Stackdriver Propagation
        .build();
    //   and map injector
    var injector = tracing.propagation().<Map<String, String>>injector(Map::put);
    // When
    var span = tracing.tracer().newTrace().start();
    span.finish(); // span context available
    // Then
    //  injected code has B3 propagation format.
    var carrier = new LinkedHashMap<String, String>();
    injector.inject(span.context(), carrier);
    System.out.println("GCP: "+carrier);
  }
}
