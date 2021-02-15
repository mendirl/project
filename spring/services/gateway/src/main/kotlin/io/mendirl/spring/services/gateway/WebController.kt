package io.mendirl.spring.services.gateway

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class WebController {

    @RequestMapping("/greeting")
    fun greeting(
        @AuthenticationPrincipal oidcUser: OidcUser, model: Model,
        @RegisteredOAuth2AuthorizedClient("okta") client: OAuth2AuthorizedClient
    ): String {
        model.addAttribute("username", oidcUser.email)
        model.addAttribute("idToken", oidcUser.idToken)
        model.addAttribute("accessToken", client.accessToken)
        return "greeting"
    }
}
