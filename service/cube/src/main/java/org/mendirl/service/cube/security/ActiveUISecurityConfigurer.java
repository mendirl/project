package org.mendirl.service.cube.security;

import org.mendirl.service.cube.CubeSecurityConfiguration.AWebSecurityConfigurer;
import org.mendirl.service.cube.configuration.ActiveUIResourceServerConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@Order(1)
public class ActiveUISecurityConfigurer extends AWebSecurityConfigurer {

    public ActiveUISecurityConfigurer(ApplicationContext context) {
        super(context);
    }

    @Override
    protected void doConfigure(HttpSecurity http) throws Exception {
        // Permit all on ActiveUI resources and the root (/) that redirects to ActiveUI index.html.
        final String pattern = "^(.{0}|\\/|\\/" + ActiveUIResourceServerConfig.NAMESPACE + "(\\/.*)?)$";
        http
            // Only theses URLs must be handled by this HttpSecurity
            .regexMatcher(pattern)
            .authorizeRequests()
            // The order of the matchers matters
            .regexMatchers(HttpMethod.OPTIONS, pattern)
            .permitAll()
            .regexMatchers(HttpMethod.GET, pattern)
            .permitAll();

        // Authorizing pages to be embedded in iframes to have ActiveUI in ActiveMonitor UI
        http.headers().frameOptions().disable();
    }
}
