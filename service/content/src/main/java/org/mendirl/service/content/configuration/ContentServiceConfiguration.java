package org.mendirl.service.content.configuration;

import com.qfs.content.cfg.IContentServiceConfig;
import com.qfs.content.service.IContentService;
import com.qfs.content.service.impl.InMemoryContentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentServiceConfiguration implements IContentServiceConfig {

    @Override
    @Bean
    public IContentService contentService() {
        return new InMemoryContentService();
    }
}
