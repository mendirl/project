package io.mendirl.spring.services.consumer

import io.mendirl.spring.services.common.JupiterProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(JupiterProperties::class)
@SpringBootApplication
class ConsumerApplication

fun main(args: Array<String>) {
//    BlockHound.install()
    runApplication<ConsumerApplication>(*args)
}
