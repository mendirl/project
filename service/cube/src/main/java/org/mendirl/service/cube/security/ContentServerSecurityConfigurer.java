package org.mendirl.service.cube.security;

import com.qfs.QfsWebUtils;
import com.qfs.content.cfg.impl.ContentServerRestServicesConfig;
import org.mendirl.service.cube.CubeSecurityConfiguration.AWebSecurityConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static org.mendirl.service.cube.CubeSecurityConfiguration.ROLE_USER;

@Configuration
@Order(4)
public class ContentServerSecurityConfigurer extends AWebSecurityConfigurer {


    public ContentServerSecurityConfigurer(ApplicationContext context) {
        super(context);
    }

    @Override
    protected final void doConfigure(final HttpSecurity http) throws Exception {
        final String url = ContentServerRestServicesConfig.NAMESPACE;
        http
            // Only theses URLs must be handled by this HttpSecurity
            .antMatcher(url + "/**")
            .authorizeRequests()
            // The order of the matchers matters
            .antMatchers(
                HttpMethod.OPTIONS,
                QfsWebUtils.url(ContentServerRestServicesConfig.REST_API_URL_PREFIX + "**"))
            .permitAll()
            .antMatchers(url + "/**")
            .hasAuthority(ROLE_USER)
            .and()
            .httpBasic();
    }
}
