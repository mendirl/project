package reactivehttp;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ReactiveClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReactiveClientApplication.class, args);
    }
}

@Component
@Log4j2
@RequiredArgsConstructor
class OnStartUp {

    private final WebClient.Builder builder;

    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        var executor = Executors.newFixedThreadPool(3);

//        executor.submit(this::httpClienBlockingCalls);
//        executor.submit(this::webClientCalls);
        executor.submit(this::httpClientCalls);

        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    private void webClientCalls() {
        var webClient = builder.baseUrl("http://localhost:8080").build();

        // dont work
//        webClient.get()
//            .uri("/greetingsinfinityflux")
//            .retrieve()
//            .bodyToFlux(ServerSentEvent.class)
//            .subscribe(content -> log.info("WebClient infinity flux: {}", content.data()));

        // dont work
//        webClient.get()
//            .uri("/greetingsinfinityflux")
//            .accept(MediaType.TEXT_EVENT_STREAM)
//            .retrieve()
//            .bodyToFlux(ServerSentEvent.class)
//            .subscribe(content -> log.info("WebClient infinity flux with accept sse: {}", content.data()));

//        WebClient infinity flux: {"message":"Hello fabien flux sse @ 2020-11-25T21:24:02.615021700Z"} == String
//        WebClient infinity flux: {message=Hello fabien flux sse @ 2020-11-25T21:25:16.333904700Z} == SSE.data()
        webClient.get()
            .uri("/greetingsinfinityfluxassse")
            .retrieve()
            .bodyToFlux(ServerSentEvent.class)
            .subscribe(content -> log.info("WebClient infinity flux: {}", content.data()));


//        webClient.get()
//            .uri("/greetingsinfinityfluxassse")
//            .accept(MediaType.TEXT_EVENT_STREAM)
//            .retrieve()
//            .bodyToFlux(ServerSentEvent.class)
//            .subscribe(content -> log.info("WebClient infinity flux sse with accept sse: {}", content.data()));

//        webClient.get()
//            .uri("/greetingsinfinityfluxassse")
//            .accept(MediaType.APPLICATION_JSON)
//            .retrieve()
//            .bodyToFlux(ServerSentEvent.class)
//            .subscribe(content -> log.info("WebClient infinity flux see with accept json: {}", content.data()));
//
//
//        webClient.get()
//            .uri("/greetingsonemono")
//            .retrieve()
//            .bodyToFlux(String.class)
//            .subscribe(content -> log.info("WebClient one mono: {}", content));
//
//        webClient.get()
//            .uri("/greetingsoneflux")
//            .retrieve()
//            .bodyToFlux(String.class)
//            .subscribe(content -> log.info("WebClient one flux: {}", content));
    }

    @SneakyThrows
    private void httpClienBlockingCalls() {
        var httpClient = HttpClient.newBuilder().build();

//        var request1 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityflux"))
//            .GET().build();
//        var response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
//        log.info("HttpClient block infinity flux:" + response1.body());

//        var request2 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityflux"))
//            .GET().header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE).build();
//        var response2 = httpClient.send(request2, HttpResponse.BodyHandlers.ofString());
//        log.info("HttpClient block infinity flux with accept sse:" + response2.body());

//        var request3 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityfluxassse"))
//            .GET().build();
//        var response3 = httpClient.send(request3, HttpResponse.BodyHandlers.ofString());
//        log.info("HttpClient block infinity flux sse:" + response3.body());

//        var request4 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityfluxassse"))
//            .GET().header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE).build();
//        var response4 = httpClient.send(request4, HttpResponse.BodyHandlers.ofString());
//        log.info("HttpClient block infinity flux sse with accept sse:" + response4.body());

//        var request5 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityfluxassse"))
//            .GET().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();
//        var response5 = httpClient.send(request5, HttpResponse.BodyHandlers.ofString());
//        log.info("HttpClient block infinity flux sse with accept json:" + response5.body());

        var request6 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsonemono"))
            .GET().build();
        var response6 = httpClient.send(request6, HttpResponse.BodyHandlers.ofString());
        log.info("HttpClient block one mono:" + response6.body());

        var request7 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsoneflux"))
            .GET().build();
        var response7 = httpClient.send(request7, HttpResponse.BodyHandlers.ofString());
        log.info("HttpClient block one flux:" + response7.body());
    }

    @SneakyThrows
    private void httpClientCalls() {
        var httpClient = HttpClient.newBuilder().build();

//        var request1 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityflux"))
//            .GET().build();
//        var response1 = httpClient.sendAsync(request1, HttpResponse.BodyHandlers.ofString());
//        response1.thenAccept(response -> log.info("HttpClient reactive infinity flux:" + response.body()));

//        var request2 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityflux"))
//            .GET().header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE).build();
//        var response2 = httpClient.sendAsync(request2, HttpResponse.BodyHandlers.ofString());
//        response2.thenAccept(response -> log.info("HttpClient reactive infinity flux with accept sse:" + response.body()));

//        var request3 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityfluxassse"))
//            .GET().build();
//        var response3 = httpClient.sendAsync(request3, HttpResponse.BodyHandlers.ofString());
//        response3.thenAccept(response -> log.info("HttpClient reactive infinity flux sse:" + response.body()));

        var request4 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityfluxassse"))
            .GET()
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
            .build();
        httpClient
            .sendAsync(request4, HttpResponse.BodyHandlers.ofString())
            .thenApply(stringHttpResponse -> {
                log.info("HttpClient reactive infinity flux sse with accept sse:" + stringHttpResponse);
                return stringHttpResponse;
            })
            .thenAccept(response -> log.info("HttpClient reactive infinity flux sse with accept sse:" + response.body()));

//        var request5 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsinfinityfluxassse"))
//            .GET().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE).build();
//        var response5 = httpClient.sendAsync(request5, HttpResponse.BodyHandlers.ofString());
//        response5.thenAccept(response -> log.info("HttpClient reactive infinity flux sse with accept json:" + response.body()));

//        var request6 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsonemono"))
//            .GET().build();
//        var response6 = httpClient.sendAsync(request6, HttpResponse.BodyHandlers.ofString());
//        response6.thenAccept(response -> log.info("HttpClient reactive one mono:" + response.body()));

//        var request7 = HttpRequest.newBuilder(URI.create("http://localhost:8080/greetingsoneflux"))
//            .GET().build();
//        var response7 = httpClient.sendAsync(request7, HttpResponse.BodyHandlers.ofString());
//        response7.thenAccept(response -> log.info("HttpClient reactive one flux:" + response.body()));
    }

}

