package io.mendirl.spring.services.producer

import io.mendirl.spring.services.common.JupiterProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication


@EnableConfigurationProperties(JupiterProperties::class)
@SpringBootApplication
class ProducerApplication

fun main(args: Array<String>) {
//    BlockHound.install()
    runApplication<ProducerApplication>(*args)
}
