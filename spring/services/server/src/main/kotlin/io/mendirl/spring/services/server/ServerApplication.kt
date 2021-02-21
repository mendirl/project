package io.mendirl.spring.services.server

import de.codecentric.boot.admin.server.config.EnableAdminServer
import io.mendirl.spring.services.common.JupiterProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAdminServer
@EnableConfigurationProperties(JupiterProperties::class)
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
