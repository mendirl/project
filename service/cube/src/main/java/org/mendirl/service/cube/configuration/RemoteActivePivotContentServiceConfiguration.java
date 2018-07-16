package org.mendirl.service.cube.configuration;

import com.qfs.jwt.service.impl.JwtUserAuthenticator;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.security.impl.AAuthenticator;
import com.qfs.security.impl.JwtAuthenticator;
import com.qfs.server.cfg.IJwtConfig;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import com.qfs.server.cfg.i18n.impl.ContentServiceI18nConfig;
import com.qfs.server.cfg.impl.CxfServletConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mendirl.service.cube.CubeSecurityConfiguration.PIVOT_USER;

@Configuration
@Import(value = {ContentServiceI18nConfig.class})
@Profile({"remote-content"})
public class RemoteActivePivotContentServiceConfiguration implements IActivePivotContentServiceConfig {

    @Value("${contentServer.remote.api.uri}")
    private String remoteContentServer;

    @Autowired
    private IJwtConfig jwtConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @DependsOn(CxfServletConfig.BEAN_NAME)
    @Bean(destroyMethod = "close")
    @Override
    public IActivePivotContentService activePivotContentService() {
        // You must populate the remote content server with PushToContentServer utility class before
        // starting the pivot sandbox.
        return new ActivePivotContentServiceBuilder()
            .remote(remoteContentServer, new JwtAuthenticator(this.jwtConfig.jwtService()), pivotAuthenticator())
            .withCacheForEntitlements(-1).build();

    }

    private AAuthenticator pivotAuthenticator() {
        return new JwtUserAuthenticator(
            PIVOT_USER,
            this.jwtConfig.jwtService(),
            userDetailsService);
    }
}
