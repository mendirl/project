package org.mendirl.service.content;

import com.qfs.content.cfg.impl.ContentServerResourceServerConfig;
import com.qfs.content.cfg.impl.ContentServerWebSocketServicesConfig;
import com.qfs.content.cfg.impl.StandaloneContentServerRestConfig;
import com.qfs.server.cfg.impl.JwtConfig;
import com.qfs.server.cfg.impl.JwtRestServiceConfig;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import(value = {
    StandaloneContentServerRestConfig.class,
    ContentServerWebSocketServicesConfig.class,
    ContentServerResourceServerConfig.class,
    JwtRestServiceConfig.class,
    JwtConfig.class})
@PropertySource(value = {"classpath:jwt.properties"})
public class ContentConfiguration {

    static {
        Registry.setContributionProvider(new ClasspathContributionProvider());
    }


}
