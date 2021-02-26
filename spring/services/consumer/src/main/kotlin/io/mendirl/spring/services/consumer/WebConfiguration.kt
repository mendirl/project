package io.mendirl.spring.services.consumer

import io.mendirl.spring.services.common.JupiterProperties
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebConfiguration {

    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ReactiveClientRegistrationRepository,
        authorizedClientService: ReactiveOAuth2AuthorizedClientService
    ): ReactiveOAuth2AuthorizedClientManager {
        val authorizedClientProvider =
            ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build()

        val authorizedClientManager =
            AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientService
            )
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider)

        return authorizedClientManager
    }

    @Profile("!cloud")
    @Bean
    fun webClient(
        builder: WebClient.Builder,
        jupiterProperties: JupiterProperties,
        authorizedClientManager: ReactiveOAuth2AuthorizedClientManager
    ): WebClient =
        client(builder, jupiterProperties, oauth2Filter(authorizedClientManager))

    @Profile("cloud")
    @Bean
    fun webClientLoadBalanced(
        builder: WebClient.Builder,
        jupiterProperties: JupiterProperties,
        authorizedClientManager: ReactiveOAuth2AuthorizedClientManager,
        lbFunction: ReactorLoadBalancerExchangeFilterFunction
    ): WebClient =
        client(builder, jupiterProperties, oauth2Filter(authorizedClientManager), lbFunction)

    private fun oauth2Filter(authorizedClientManager: ReactiveOAuth2AuthorizedClientManager): ExchangeFilterFunction {
        val oauth2Client = ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        oauth2Client.setDefaultClientRegistrationId("consumer-client")
        return oauth2Client
    }

    private fun client(
        builder: WebClient.Builder,
        jupiterProperties: JupiterProperties,
        vararg filters: ExchangeFilterFunction
    ): WebClient {
        filters.forEach { builder.filter(it) }
        return builder
            .baseUrl(jupiterProperties.applications["producer"]!!.url)
            .build()
    }

}
