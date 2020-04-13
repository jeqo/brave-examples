# Tracing

Examples for Brave Tracing API. 

## Properties

### Local Service: Name and Endpoint (IP and Port)

Represents the name and location of a traced service. This tends to be static during runtime. Defaults to `unknown`. 

```java
import brave.Tracing;
public class App {
    public static void main(String[] args){
        Tracing tracing = Tracing.newBuilder()
                                 .localServiceName("service-users")
                                 .build();
        System.out.println(tracing);
    }
}
```

### Propagation Factory

Defines which propagation format to use when propagating trace context. This is dependent on the Tracing infrastructure. By default, it uses Zipkin [B3](https://github.com/openzipkin/b3-propagation). Other examples are: (AWS)[https://github.com/openzipkin/zipkin-aws/tree/master/brave-propagation-aws], (GCP)[https://github.com/openzipkin/zipkin-gcp/tree/master/propagation-stackdriver].

```java
import brave.Tracing;import brave.propagation.B3Propagation;
public class App {
    public static void main(String[] args){
        Tracing tracing = Tracing.newBuilder()
                                 .localServiceName("service-users")
                                 .propagationFactory(B3Propagation.FACTORY)
                                 .build();
        System.out.println(tracing);
    }
}
```

Propagation brings 2 important components: Injector and Extractor. `Injector` injects context on a carrier when leaving a service context. Similarly, `Extractor` extracts context from a carrier when entering a service context.

[More info](https://github.com/openzipkin/brave/tree/master/brave#propagation)

## Components

### Tracer

//TODO

#### Properties

##### no-op

#### Components

##### Pending Spans

###### Components

#### Features

##### Always Sample Local

### Propagation

//TODO

### Sampler

//TODO

### Current Trace Context

//TODO

### Clock

//TODO

### Error Parser

//TODO

### Span Reporter

//TODO

### Finished Span Handlers

//TODO

### Span Listeners

//TODO

## Features

### Trace ID 128-bit

//TODO

### Support Join

//TODO

### Always Report Spans

//TODO

### Track Orphans

//TODO