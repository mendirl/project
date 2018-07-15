package org.mendirl.service.content;

import com.qfs.content.service.IContentService;
import com.qfs.security.cfg.ICorsFilterConfig;
import com.qfs.server.cfg.IJwtConfig;
import com.qfs.servlet.handlers.impl.NoRedirectLogoutSuccessHandler;
import com.quartetfs.biz.pivot.security.IAuthorityComparator;
import com.quartetfs.biz.pivot.security.impl.AuthorityComparatorAdapter;
import com.quartetfs.fwk.ordering.impl.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import javax.servlet.Filter;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
public class ContentSecurityConfiguration {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_TECH = "ROLE_TECH";

    private static final String ROLE_KPI = "ROLE_KPI";

    private static final String ROLE_CS_ROOT = IContentService.ROLE_ROOT;

    private static final String[] PIVOT_USER_ROLES = {ROLE_TECH, ROLE_CS_ROOT};

    public static final String CS_COOKIE_NAME = "CS_JSESSIONID";

    public static final String BASIC_AUTH_BEAN_NAME = "basicAuthenticationEntryPoint";

    private IJwtConfig jwtConfig;

    public ContentSecurityConfiguration(IJwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.eraseCredentials(false)
            // Add an LDAP authentication provider instead of this to support LDAP
            .userDetailsService(userDetailsService()).and()
            // Required to allow JWT
            .authenticationProvider(jwtConfig.jwtAuthenticationProvider());
    }

    @Bean(name = BASIC_AUTH_BEAN_NAME)
    public AuthenticationEntryPoint basicAuthenticationEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    @Bean
    public IAuthorityComparator authorityComparator() {
        final CustomComparator<String> customComparator = new CustomComparator<>();

        customComparator.setFirstObjects(Collections.singletonList(ROLE_USER));
        customComparator.setLastObjects(Collections.singletonList(ROLE_ADMIN));

        return new AuthorityComparatorAdapter(customComparator);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User.withUsername("admin").password("{noop}admin").authorities(ROLE_USER, ROLE_ADMIN, ROLE_KPI, ROLE_CS_ROOT).build());
        manager.createUser(User.withUsername("user1").password("{noop}user1").authorities(ROLE_USER, ROLE_KPI, "ROLE_DESK_A").build());
        manager.createUser(User.withUsername("user2").password("{noop}user2").authorities(ROLE_USER, "ROLE_EUR_USD").build());
        manager.createUser(User.withUsername("manager1").password("{noop}manager1").authorities(ROLE_USER, ROLE_KPI).build());
        manager.createUser(User.withUsername("manager2").password("{noop}manager2").authorities(ROLE_USER, ROLE_KPI).build());
        manager.createUser(User.withUsername("live").password("live").authorities(ROLE_TECH).build());
        manager.createUser(User.withUsername("pivot").password("pivot").authorities(PIVOT_USER_ROLES).build());

        return manager;

    }

    public abstract static class AWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

        private final boolean logout;
        private final String cookieName;

        private ApplicationContext context;

        public AWebSecurityConfigurer(ApplicationContext context) {
            this(context, null);
        }

        public AWebSecurityConfigurer(ApplicationContext context, String cookieName) {
            this.logout = cookieName != null;
            this.cookieName = cookieName;
            this.context = context;
        }

        @Override
        protected final void configure(final HttpSecurity http) throws Exception {
            Filter jwtFilter = context.getBean(IJwtConfig.class).jwtFilter();
            Filter corsFilter = context.getBean(ICorsFilterConfig.class).corsFilter();

            http
                // As of Spring Security 4.0, CSRF protection is enabled by default.
                .csrf().disable()
                // Configure CORS
                .addFilterBefore(corsFilter, SecurityContextPersistenceFilter.class)
                // To allow authentication with JWT (Required for ActiveUI)
                .addFilterAfter(jwtFilter, SecurityContextPersistenceFilter.class);

            if (logout) {
                // Configure logout URL
                http.logout()
                    .permitAll()
                    .deleteCookies(cookieName)
                    .invalidateHttpSession(true)
                    .logoutSuccessHandler(new NoRedirectLogoutSuccessHandler());
            }

            doConfigure(http);
        }

        protected abstract void doConfigure(HttpSecurity http) throws Exception;

    }
}
