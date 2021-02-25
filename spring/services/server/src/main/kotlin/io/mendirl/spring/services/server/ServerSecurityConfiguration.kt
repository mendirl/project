package io.mendirl.spring.services.server


import de.codecentric.boot.admin.server.config.AdminServerProperties
import io.mendirl.spring.services.common.JupiterProperties
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.autoconfigure.security.reactive.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
    val adminServerProperties: AdminServerProperties
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            .authorizeExchange {
                it
                    // web resources
                    .matchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    // actuator
                    .matchers(EndpointRequest.toAnyEndpoint()).permitAll()
                    // sba
                    .pathMatchers(adminServerProperties.contextPath).permitAll()
                    .pathMatchers("${adminServerProperties.contextPath}/**").permitAll()
                    // anything else
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { it.jwt().jwtAuthenticationConverter(jwtConverter()) }
            .build()

    fun jwtConverter(): Converter<Jwt, Mono<AbstractAuthenticationToken>> {
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setPrincipalClaimName(jupiterProperties.security.oauth2.userNameAttribute)
        return ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter)
    }

}
