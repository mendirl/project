package io.mendirl.spring.services.common

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "jupiter")
data class JupiterProperties(
    var securityOauth2Url: String,
    var applications: Map<String, Application>
)

data class Application(
    val port: Int,
    val url: String
)
