package org.mendirl.service.cube.security;

import com.qfs.security.cfg.ICorsFilterConfig;
import com.qfs.server.cfg.impl.JwtConfig;
import com.qfs.server.cfg.impl.JwtRestServiceConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.Filter;

import static org.mendirl.service.cube.CubeSecurityConfiguration.BASIC_AUTH_BEAN_NAME;
import static org.mendirl.service.cube.CubeSecurityConfiguration.ROLE_USER;

/**
 * Configuration for JWT.
 * <p>
 * The most important point is the {@code authenticationEntryPoint}. It must
 * only send an unauthorized status code so that JavaScript clients can
 * authenticate (otherwise the browser will intercepts the response).
 *
 * @author Quartet FS
 * @see HttpStatusEntryPoint
 */
@Configuration
@Order(2) // Must be done before ContentServerSecurityConfigurer (because they match common URLs)
@Import(value = {JwtRestServiceConfig.class, JwtConfig.class})
@PropertySource(value = {"classpath:jwt.properties"})
public class JwtSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private ApplicationContext context;

    public JwtSecurityConfigurer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final Filter corsFilter = context.getBean(ICorsFilterConfig.class).corsFilter();
        final AuthenticationEntryPoint basicAuthenticationEntryPoint = context.getBean(
            BASIC_AUTH_BEAN_NAME,
            AuthenticationEntryPoint.class);
        http.antMatcher(JwtRestServiceConfig.REST_API_URL_PREFIX + "/**")
            // As of Spring Security 4.0, CSRF protection is enabled by default.
            .csrf().disable()
            // Configure CORS
            .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .antMatchers("/**").hasAnyAuthority(ROLE_USER)
            .and()
            .httpBasic().authenticationEntryPoint(basicAuthenticationEntryPoint);
    }
}
