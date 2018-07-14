package org.mendirl.service.cube.activeviam;

import com.qfs.content.service.IContentService;
import com.qfs.content.service.impl.InMemoryContentService;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mendirl.service.cube.CubeSecurityConfiguration.ROLE_USER;

//@Import(value = {ContentServerResourceServerConfig.class})
@Configuration
public class ActivePivotContentServiceConfiguration implements IActivePivotContentServiceConfig {

    @Bean
    @Override
    public IActivePivotContentService activePivotContentService() {
        return new ActivePivotContentServiceBuilder().with(contentService()).withCacheForEntitlements(-1)
            .needInitialization(ROLE_USER, ROLE_USER).build();
    }

    @Bean
    @Override
    public IContentService contentService() {
        return new InMemoryContentService();
    }
}
