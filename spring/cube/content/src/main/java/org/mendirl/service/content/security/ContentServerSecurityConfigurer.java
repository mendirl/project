package org.mendirl.service.content.security;

import org.mendirl.service.content.ContentSecurityConfiguration.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static com.qfs.content.cfg.impl.ContentServerRestServicesConfig.PING_SUFFIX;
import static com.qfs.content.cfg.impl.ContentServerRestServicesConfig.REST_API_URL_PREFIX;
import static org.mendirl.service.content.ContentSecurityConfiguration.*;

@Configuration
@Order(3)
public class ContentServerSecurityConfigurer extends AWebSecurityConfigurer {

    public ContentServerSecurityConfigurer(ApplicationContext context) {
        super(context, CS_COOKIE_NAME);
    }

    @Override
    protected void doConfigure(final HttpSecurity http) throws Exception {
        // The order of antMatchers does matter!
        http.authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .permitAll()
            // Ping service used by ActiveUI (not protected)
            .antMatchers(REST_API_URL_PREFIX + PING_SUFFIX)
            .permitAll()
            .antMatchers("/**")
            .hasAnyAuthority(ROLE_USER, ROLE_TECH)
            .and()
            .httpBasic();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
