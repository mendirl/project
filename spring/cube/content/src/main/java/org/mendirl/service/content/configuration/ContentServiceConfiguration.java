package org.mendirl.service.content.configuration;

import com.qfs.content.cfg.IContentServiceConfig;
import com.qfs.content.service.IContentService;
import com.qfs.content.service.audit.impl.AuditableHibernateContentService;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.tool.schema.Action;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class ContentServiceConfiguration implements IContentServiceConfig {

    private DataSource dataSource;

    public ContentServiceConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
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
