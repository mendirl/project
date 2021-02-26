package io.mendirl.spring.services.producer

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Instant
import kotlin.random.Random

@RestController
class WebController {

    private val logger = LoggerFactory.getLogger(ProducerApplication::class.java)

    @GetMapping("/api/position")
    fun position(): Mono<Position> {
        val position = Position("position ${Random.nextInt() % 1000}", Instant.now())
        logger.info("position generated: $position")
        return Mono.just(position)
    }

}

data class Position(val name: String, val date: Instant)
