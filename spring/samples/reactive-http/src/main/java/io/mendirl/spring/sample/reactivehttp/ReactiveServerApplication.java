package io.mendirl.spring.sample.reactivehttp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class ReactiveServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveServerApplication.class, args);
    }

    @Bean
    RouterFunction<ServerResponse> routes(GreetingHandlers greetingHandlers) {
        return route()
            .GET("/greetingsinfinityflux", greetingHandlers.greetingsinfinityflux())
            .GET("/greetingsinfinityfluxassse", greetingHandlers.greetingsinfinityfluxassse())
            .GET("/greetingsonemono", greetingHandlers.greetingsonemono())
            .GET("/greetingsoneflux", greetingHandlers.greetingsoneflux())
            .build();
    }

}


@Component
@RequiredArgsConstructor
class GreetingHandlers {

    private final GreetingService greetingService;

    public HandlerFunction<ServerResponse> greetingsinfinityflux() {
        return request -> ok()
            .body(greetingService.greetingsinfinityflux(new GreetingRequest("fabien flux")).log(), GreetingResponse.class);
    }

    // Content-Type == text/event-stream is mandatory for Flux is not finite
    public HandlerFunction<ServerResponse> greetingsinfinityfluxassse() {
        return request -> ok().contentType(MediaType.TEXT_EVENT_STREAM)
            .body(greetingService.greetingsinfinityflux(new GreetingRequest("fabien flux sse")).log(), GreetingResponse.class);
    }

    public HandlerFunction<ServerResponse> greetingsonemono() {
        return request -> ok().body(greetingService.greetingsonemono().log(), GreetingResponse.class);
    }

    public HandlerFunction<ServerResponse> greetingsoneflux() {
        return request -> ok().body(greetingService.greetingsoneflux().log(), GreetingResponse.class);
    }
}

@Service
class GreetingService {

    Flux<GreetingResponse> greetingsinfinityflux(GreetingRequest request) {
        return Flux
            .fromStream(Stream.generate(() -> new GreetingResponse("Hello " + request.getName() + " @ " + Instant.now())))
            .delayElements(Duration.ofSeconds(1));
    }

    public Flux<GreetingResponse> greetingsoneflux() {
        return Flux.just(new GreetingResponse("Hello fabien flux @ " + Instant.now()));
    }

    public Mono<GreetingResponse> greetingsonemono() {
        return Mono.just(new GreetingResponse("Hello fabien mono @ " + Instant.now()));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingRequest {
    private String name;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class GreetingResponse {
    private String message;
}
