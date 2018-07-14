package org.mendirl.service.cube.activeviam;

import com.qfs.security.IBranchPermissions;
import com.qfs.security.IBranchPermissionsManager;
import com.qfs.security.impl.BranchPermissionsManager;
import com.qfs.server.cfg.IActivePivotBranchPermissionsManagerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.mendirl.service.cube.CubeSecurityConfiguration.ROLE_ADMIN;
import static org.mendirl.service.cube.CubeSecurityConfiguration.ROLE_USER;

@Configuration
public class ActivePivotBranchPermissionsManagerConfiguration implements IActivePivotBranchPermissionsManagerConfig {

    @Bean
    @Override
    public IBranchPermissionsManager branchPermissionsManager() {
        return new BranchPermissionsManager(
            new HashSet<>(Arrays.asList(ROLE_ADMIN, ROLE_USER)),
            Collections.singleton(ROLE_ADMIN),
            IBranchPermissions.ALL_USERS_ALLOWED);
    }


}
