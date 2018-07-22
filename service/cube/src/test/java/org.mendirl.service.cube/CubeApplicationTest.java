package org.mendirl.service.cube;

import com.qfs.content.service.IContentService;
import com.qfs.content.service.impl.RemoteContentService;
import com.qfs.pivot.content.IActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentService;
import com.qfs.pivot.content.impl.ActivePivotContentServiceUtil;
import com.qfs.rest.client.impl.ABasicAuthenticator;
import com.qfs.rest.client.impl.UserAuthenticator;
import com.qfs.server.cfg.i18n.impl.I18nConfig;
import com.quartetfs.fwk.Registry;
import com.quartetfs.fwk.contributions.impl.ClasspathContributionProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mendirl.service.cube.CubeSecurityConfiguration.ROLE_USER;

public class CubeApplicationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CubeApplicationTest.class);

    @Test
    public void pushDataContent() {
        Registry.setContributionProvider(new ClasspathContributionProvider("com.activeviam", "com.quartetfs", "com.qfs", "org.mendirl.service.cube"));

        IContentService cs = getContentService();
        final IActivePivotContentService apcs = new ActivePivotContentService(cs);

        LOGGER.info("Updating content service");

        ActivePivotContentServiceUtil.initialize(apcs, ROLE_USER, ROLE_USER);
        I18nConfig.pushTranslations(apcs);

        LOGGER.info("Done");
    }

    /**
     * Retrieves the content service.
     *
     * @return The content service.
     */
    private static IContentService getContentService() {
        final ABasicAuthenticator contentServerAuthenticator = new UserAuthenticator("admin", "admin");

        final String remoteContentServiceUrl = "http://localhost:8787/content";

        return new RemoteContentService(
            remoteContentServiceUrl,
            contentServerAuthenticator,
            contentServerAuthenticator);
    }
}
