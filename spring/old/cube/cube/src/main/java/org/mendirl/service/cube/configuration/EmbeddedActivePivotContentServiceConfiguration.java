package org.mendirl.service.cube.configuration;

import com.qfs.content.cfg.impl.ContentServerResourceServerConfig;
import com.qfs.content.service.IContentService;
import com.qfs.content.service.audit.impl.AuditableHibernateContentService;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceBuilder;
import com.qfs.server.cfg.content.IActivePivotContentServiceConfig;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.tool.schema.Action;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.Properties;

import static org.mendirl.service.cube.CubeSecurityConfiguration.ROLE_USER;

@Configuration
@Import(value = {
    ContentServerResourceServerConfig.class
})
@Profile({"embedded-content"})
public class EmbeddedActivePivotContentServiceConfiguration implements IActivePivotContentServiceConfig {

    private DataSource dataSource;

    public EmbeddedActivePivotContentServiceConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @Override
    public IActivePivotContentService activePivotContentService() {
        return new ActivePivotContentServiceBuilder().with(contentService()).withCacheForEntitlements(-1)
            .needInitialization(ROLE_USER, ROLE_USER).build();
    }

    @Bean
    @Override
    public IContentService contentService() {
        Properties properties = new Properties();

        properties.put(AvailableSettings.DIALECT, H2Dialect.class.getCanonicalName());
        properties.put(AvailableSettings.DATASOURCE, dataSource);
        properties.put(AvailableSettings.HBM2DDL_AUTO, Action.UPDATE);

        org.hibernate.cfg.Configuration conf = new org.hibernate.cfg.Configuration();
        conf.addProperties(properties);

        return new AuditableHibernateContentService(conf);
    }

}
