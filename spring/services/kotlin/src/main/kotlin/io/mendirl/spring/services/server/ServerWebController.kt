package io.mendirl.spring.services.server

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/callme")
class ServerWebController {

    @PreAuthorize("hasAuthority('SCOPE_profile')")
    @GetMapping("/ping")
    fun ping(): Mono<String> =
        ReactiveSecurityContextHolder.getContext()
            .map { "${it.authentication.name} has scopes: ${it.authentication.authorities}" }

}
