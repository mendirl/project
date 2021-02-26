package io.mendirl.spring.services.consumer

import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Instant

@Component
class ServerClient(
    val webClient: WebClient,
    val rsocketClient: RSocketRequester
) {

    companion object {
        const val CLIENT = "Client"
        const val REQUEST = "Request"
        const val FIRE_AND_FORGET = "Fire-And-Forget"
        const val STREAM = "Stream"
    }

    fun position() = webClient.get()
        .uri("/api/position")
        .retrieve()
        .bodyToMono(Position::class.java)

    fun message() =
        rsocketClient.route("request-response")
            .data(Message(CLIENT, REQUEST))
            .retrieveMono(Message::class.java)

}

data class Position(val name: String, val date: Instant)

data class Message(
    val origin: String,
    val interaction: String,
    val index: Long = 0,
    val created: Long = Instant.now().epochSecond
)
