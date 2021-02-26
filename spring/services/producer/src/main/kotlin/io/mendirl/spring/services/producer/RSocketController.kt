package io.mendirl.spring.services.producer

import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.time.Instant


@Controller
internal class RSocketController {

    private val logger = LoggerFactory.getLogger(RSocketController::class.java)

    companion object {
        const val SERVER = "Server"
        const val RESPONSE = "Response"
    }

    @MessageMapping("request-response")
    fun requestResponse(request: Message): Message {
        logger.info("Received request-response request: $request")
        // create a single Message and return it
        return Message(SERVER, RESPONSE)
    }

}

data class Message(
    val origin: String,
    val interaction: String,
    val index: Long = 0,
    val created: Long = Instant.now().epochSecond
)
