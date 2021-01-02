package org.mendirl.service.cube.security;

import com.qfs.server.cfg.IActivePivotConfig;
import org.mendirl.service.cube.CubeSecurityConfiguration.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

import static com.qfs.QfsWebUtils.url;
import static com.qfs.server.cfg.impl.ActivePivotRemotingServicesConfig.*;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.PING_SUFFIX;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.REST_API_URL_PREFIX;
import static com.qfs.server.cfg.impl.ActivePivotServicesConfig.*;
import static com.qfs.server.cfg.impl.CxfServletConfig.CXF_WEB_SERVICES;
import static org.mendirl.service.cube.CubeSecurityConfiguration.*;

@Configuration
@Order(5)
public class ActivePivotSecurityConfigurer extends AWebSecurityConfigurer {

    private IActivePivotConfig activePivotConfig;

    public ActivePivotSecurityConfigurer(ApplicationContext context, IActivePivotConfig activePivotConfig) {
        super(context, AP_COOKIE_NAME);
        this.activePivotConfig = activePivotConfig;
    }

    @Override
    protected void doConfigure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            // The order of the matchers matters
            .antMatchers(HttpMethod.OPTIONS, REST_API_URL_PREFIX + "/**")
            .permitAll()
            // Web services used by AP live 3.4
            .antMatchers(CXF_WEB_SERVICES + '/' + ID_GENERATOR_SERVICE + "/**")
            .hasAnyAuthority(ROLE_USER, ROLE_TECH)
            .antMatchers(CXF_WEB_SERVICES + '/' + LONG_POLLING_SERVICE + "/**")
            .hasAnyAuthority(ROLE_USER, ROLE_TECH)
            .antMatchers(CXF_WEB_SERVICES + '/' + LICENSING_SERVICE + "/**")
            .hasAnyAuthority(ROLE_USER, ROLE_TECH)
            // Spring remoting services used by AP live 3.4
            .antMatchers(url(ID_GENERATOR_REMOTING_SERVICE, "**"))
            .hasAnyAuthority(ROLE_USER, ROLE_TECH)
            .antMatchers(url(LONG_POLLING_REMOTING_SERVICE, "**"))
            .hasAnyAuthority(ROLE_USER, ROLE_TECH)
            .antMatchers(url(LICENSING_REMOTING_SERVICE, "**"))
            .hasAnyAuthority(ROLE_USER, ROLE_TECH)
            // The ping service is temporarily authenticated (see PIVOT-3149)
            .antMatchers(url(REST_API_URL_PREFIX, PING_SUFFIX))
            .hasAnyAuthority(ROLE_USER, ROLE_TECH)
            // REST services
            .antMatchers(REST_API_URL_PREFIX + "/**")
            .hasAnyAuthority(ROLE_USER)
            // One has to be a user for all the other URLs
            .antMatchers("/**")
            .hasAuthority(ROLE_USER)
            .and()
            .httpBasic()
            // SwitchUserFilter is the last filter in the chain. See FilterComparator class.
            .and()
            .addFilterAfter(activePivotConfig.contextValueFilter(), SwitchUserFilter.class);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
