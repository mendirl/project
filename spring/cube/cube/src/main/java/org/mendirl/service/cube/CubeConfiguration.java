package org.mendirl.service.cube;

import com.qfs.distribution.security.IDistributedSecurityManager;
import com.qfs.messenger.IDistributedMessenger;
import com.qfs.server.cfg.IActivePivotConfig;
import com.qfs.server.cfg.IDatastoreConfig;
import com.qfs.server.cfg.i18n.impl.LocalI18nConfig;
import com.qfs.server.cfg.impl.*;
import com.quartetfs.biz.pivot.monitoring.impl.DynamicActivePivotManagerMBean;
import com.quartetfs.fwk.AgentException;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;
import com.quartetfs.fwk.monitoring.jmx.impl.JMXEnabler;
import com.quartetfs.fwk.security.IUserDetailsService;
import com.quartetfs.fwk.types.impl.ExtendedPluginInjector;
import org.springframework.context.annotation.*;

import static com.quartetfs.fwk.types.impl.ExtendedPluginInjector.inject;

@Configuration
@Import(value = {
    // Core imports
    ActivePivotConfig.class,
    DatastoreConfig.class,
    ActivePivotServicesConfig.class,
    ActivePivotWebServicesConfig.class,
    ActiveViamRestServicesConfig.class,
    ActivePivotWebSocketServicesConfig.class,
    VersionServicesConfig.class,
    LocalI18nConfig.class,
    JwtRestServiceConfig.class,
    JwtConfig.class
})
@PropertySource(value = {"classpath:jwt.properties"})
public class CubeConfiguration {

    static {
        Registry.setContributionProvider(new ClasspathContributionProvider("com.activeviam", "com.quartetfs", "com.qfs", "org.mendirl.service.cube"));
    }

    /**
     * Initialize and start the ActivePivot Manager, after performing all the injections into the
     * ActivePivot plug-ins.
     */
    @Bean
    public Void startManager(IActivePivotConfig activePivotConfig, IUserDetailsService
        qfsUserDetailsService) throws AgentException {
        /* ********************************************************************** */
        /* Inject dependencies before the ActivePivot components are initialized. */
        /* ********************************************************************** */
        apManagerInitPrerequisitePluginInjections(activePivotConfig, qfsUserDetailsService);

        /* *********************************************** */
        /* Initialize the ActivePivot Manager and start it */
        /* *********************************************** */
        activePivotConfig.activePivotManager().init(null);
        activePivotConfig.activePivotManager().start();

        return null;
    }

    /**
     * Extended plugin injections that are required before doing the startup of the ActivePivot
     * manager.
     */
    private void apManagerInitPrerequisitePluginInjections(IActivePivotConfig activePivotConfig, IUserDetailsService qfsUserDetailsService) {
        /* ********************************************************* */
        /* Core injections for distributed architecture (when used). */
        /* ********************************************************* */
        // Inject the distributed messenger with security services
        for (Object key : Registry.getExtendedPlugin(IDistributedMessenger.class).keys()) {
            ExtendedPluginInjector.inject(IDistributedMessenger.class, String.valueOf(key), activePivotConfig.contextValueManager());
        }

        // Inject the distributed security manager with security services
        for (Object key : Registry.getExtendedPlugin(IDistributedSecurityManager.class).keys()) {
            inject(IDistributedSecurityManager.class, String.valueOf(key), qfsUserDetailsService);
        }
    }

    /**
     * Enable JMX Monitoring for the Datastore
     *
     * @return the {@link JMXEnabler} attached to the datastore
     */
    @Bean
    public JMXEnabler JMXDatastoreEnabler(IDatastoreConfig datastoreConfig) {
        return new JMXEnabler(datastoreConfig.datastore());
    }

    /**
     * Enable JMX Monitoring for ActivePivot Components
     *
     * @return the {@link JMXEnabler} attached to the activePivotManager
     */
    @Bean
    @DependsOn(value = "startManager")
    public JMXEnabler JMXActivePivotEnabler(IActivePivotConfig activePivotConfig) {
        return new JMXEnabler(new DynamicActivePivotManagerMBean(activePivotConfig.activePivotManager()));
    }

}
