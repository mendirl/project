package io.mendirl.spring.services.consumer

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@EnableScheduling
@Configuration
class SchedulingConfiguration(
    val client: ServerClient
) {

    private val logger = LoggerFactory.getLogger(SchedulingConfiguration::class.java)

    @Scheduled(initialDelay = 1000, fixedRate = 1000)
    fun scheduled() {
        client.position()
            .subscribe {
                logger.info("position retrieved: $it")
            }

    }
}
