package io.mendirl.spring.services.server


import de.codecentric.boot.admin.server.config.AdminServerProperties
import io.mendirl.spring.services.common.JupiterProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class ServerSecurityConfiguration(
    val jupiterProperties: JupiterProperties,
    val adminServer: AdminServerProperties
) {

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
    @Order(200)
    fun sbaFilter(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange {
                it
                    .pathMatchers(adminServer.contextPath).permitAll()
                    .pathMatchers("${adminServer.contextPath}/**").permitAll()
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
            .oauth2ResourceServer {
                it.jwt().jwtAuthenticationConverter(jwtConverter())
            }
        return http.build()
    }

    fun jwtConverter(): Converter<Jwt, Mono<AbstractAuthenticationToken>> {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setPrincipalClaimName(jupiterProperties.security.oauth2.userNameAttribute)
        return ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter)
    }
}
