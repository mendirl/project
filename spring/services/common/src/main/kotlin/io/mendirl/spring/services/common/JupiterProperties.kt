package io.mendirl.spring.services.common

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "jupiter")
data class JupiterProperties(
    val security: Security,
    val applications: Map<String, Application>
)

data class Application(
    val port: Int,
    val url: String
)

data class Security(val oauth2: Oauth2)

data class Oauth2(val url: String, val userNameAttribute: String)
