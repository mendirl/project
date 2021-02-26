package io.mendirl.spring.services.consumer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.rsocket.RSocketRequester


@Configuration
class RSocketConfiguration(
    val rsocketRequesterBuilder: RSocketRequester.Builder
) {

    @Bean
    fun rsocketClient() = rsocketRequesterBuilder.tcp("localhost", 7000)
}

