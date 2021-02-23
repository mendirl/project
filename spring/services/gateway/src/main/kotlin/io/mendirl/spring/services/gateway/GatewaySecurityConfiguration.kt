package io.mendirl.spring.services.gateway

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class GatewaySecurityConfiguration {

    @Bean
    @Order(1)
    fun commonFilter(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.csrf().disable()
        return http.build()
    }

    @Bean
    @Order(100)
    fun actuatorFilter(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange {
                it
                    .pathMatchers("/actuator").permitAll()
                    .pathMatchers("/actuator/**").permitAll()
            }
        return http.build()
    }

    @Bean
    @Order(1000)
    fun oauth2Filter(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange {
                it.anyExchange().authenticated()
            }
            .oauth2Login(Customizer.withDefaults())
        return http.build()
    }


}
