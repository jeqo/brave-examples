package brave.example.http;

import brave.SpanCustomizer;
import brave.Tracing;
import brave.example.common.ZipkinConfig;
import brave.http.HttpAdapter;
import brave.http.HttpClientParser;
import brave.http.HttpTracing;
import brave.okhttp3.TracingInterceptor;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

import static java.lang.System.out;

public class OkHttp3Client {
    public static void main(String[] args) {
        var reporter = ZipkinConfig.load().reporter();
        var tracing = Tracing.newBuilder()
                .localServiceName("okhttp3-client")
                .spanReporter(reporter)
                .build();
        var httpTracing = HttpTracing.newBuilder(tracing)
                .clientParser(new HttpClientParser() {
                    @Override
                    public <Req> void request(HttpAdapter<Req, ?> adapter, Req req, SpanCustomizer span) {
                        super.request(adapter, req, span); // keep same logic at the beginning
                        span.tag("additional", "message");
                    }
                })
                .build();

        var client = new OkHttpClient.Builder()
                .dispatcher(new Dispatcher(
                        httpTracing.tracing().currentTraceContext()
                                .executorService(new Dispatcher().executorService())))
                .addNetworkInterceptor(TracingInterceptor.create(httpTracing))
                .build();

        var request = new Request.Builder()
                .url("http://localhost:4567/foo")
                .build();

        try (Response response = client.newCall(request).execute()) {
            var payload = Objects.requireNonNull(response.body()).string();
            out.println(payload);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            tracing.close();
            reporter.close();
        }
    }
}
