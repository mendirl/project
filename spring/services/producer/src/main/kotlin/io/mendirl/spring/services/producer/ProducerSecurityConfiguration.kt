package io.mendirl.spring.services.producer

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

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
