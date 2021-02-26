package io.mendirl.spring.services.producer

import io.mendirl.spring.services.common.JupiterProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Instant
import kotlin.random.Random

@RestController
@EnableConfigurationProperties(JupiterProperties::class)
@SpringBootApplication
class ProducerApplication {

    private val logger = LoggerFactory.getLogger(ProducerApplication::class.java)

    @GetMapping("/api/position")
    fun position(): Mono<Position> {
        val position = Position("position ${Random.nextInt() % 1000}", Instant.now())
        logger.info("position generated: $position")
        return Mono.just(position)
    }

}

fun main(args: Array<String>) {
//    BlockHound.install()
    runApplication<ProducerApplication>(*args)
}

data class Position(val name: String, val date: Instant)

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class ProducerSecurityConfiguration {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .authorizeExchange {
                it
                    // actuator
                    .matchers(EndpointRequest.toAnyEndpoint()).permitAll()
                    // /api
                    .pathMatchers("/api/**").hasAuthority("SCOPE_position")
                    // anything else
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { it.jwt() }
            .build()

}
