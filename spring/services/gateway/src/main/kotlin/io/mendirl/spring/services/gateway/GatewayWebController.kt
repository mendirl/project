package io.mendirl.spring.services.gateway

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.WebSession
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/")
class GatewayWebController {

    @GetMapping(value = ["/token"])
    fun getHome(@RegisteredOAuth2AuthorizedClient authorizedClient: OAuth2AuthorizedClient): Mono<String> {
        return Mono.just(authorizedClient.accessToken.tokenValue)
    }

    @GetMapping
    fun index(session: WebSession): Mono<String> {
        return Mono.just(session.id)
    }

}
