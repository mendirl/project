package org.mendirl.service.cube.security;

import com.qfs.security.cfg.ICorsFilterConfig;
import com.qfs.server.cfg.impl.VersionServicesConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.Filter;

@Configuration
@Order(3)
public class VersionSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private ApplicationContext context;

    public VersionSecurityConfigurer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        Filter corsFilter = context.getBean(ICorsFilterConfig.class).corsFilter();

        http.antMatcher(VersionServicesConfig.REST_API_URL_PREFIX + "/**")
            // As of Spring Security 4.0, CSRF protection is enabled by default.
            .csrf().disable()
            // Configure CORS
            .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
            .authorizeRequests()
            .antMatchers("/**").permitAll();
    }
}
