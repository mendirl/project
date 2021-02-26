package io.mendirl.spring.services.consumer

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Instant

@Component
class ServerClient(
    val webClient: WebClient

) {


    fun position() = webClient.get()
        .uri("/api/position")
        .retrieve()
        .bodyToMono(Position::class.java)

}

data class Position(val name: String, val date: Instant)
