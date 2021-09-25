package com.ap.greenpole.usermodule.util;

import com.ap.greenpole.usermodule.config.GreenPoleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 * @date 17-Nov-20 03:23 PM
 */
@Component
public class SharedEnvironment {

    public String adminRegisterKey;

    public String noReplayMailAddress;

    public String smptUserName;

    public String supportEmailAddress;

    public String iconUrl;

    public String fineName;

    public SharedEnvironment(Environment env) {
        adminRegisterKey = env == null ? null : env.getProperty("app.admin-register-key");
        if (adminRegisterKey == null && GreenPoleConfig.userManagementProperties != null) {
            adminRegisterKey = GreenPoleConfig.resolveOptionalEnvFromSystem(
                    GreenPoleConfig.userManagementProperties.getString("app.admin-register-key", ""));
        }

        noReplayMailAddress = env == null ? null : env.getProperty("app.email.no-reply");
        if (noReplayMailAddress == null && GreenPoleConfig.userManagementProperties != null) {
            noReplayMailAddress = GreenPoleConfig.resolveOptionalEnvFromSystem(
                    GreenPoleConfig.userManagementProperties.getString("app.email.no-reply", ""));
        }

        smptUserName = env == null ? null : env.getProperty("spring.mail.username");
        if (smptUserName == null && GreenPoleConfig.userManagementProperties != null) {
            smptUserName = GreenPoleConfig.resolveOptionalEnvFromSystem(
                    GreenPoleConfig.userManagementProperties.getString("spring.mail.username", ""));
        }

        supportEmailAddress = env == null ? null : env.getProperty("app.email.support");
        if (supportEmailAddress == null && GreenPoleConfig.userManagementProperties != null) {
            supportEmailAddress = GreenPoleConfig.resolveOptionalEnvFromSystem(
                    GreenPoleConfig.userManagementProperties.getString("app.email.support", ""));
        }

        iconUrl = env == null ? null : env.getProperty("app.icon-url");
        if (iconUrl == null && GreenPoleConfig.userManagementProperties != null) {
            iconUrl = GreenPoleConfig.resolveOptionalEnvFromSystem(
                    GreenPoleConfig.userManagementProperties.getString("app.icon-url", ""));
        }

        fineName = env == null ? null : env.getProperty("app.fine-name");
        if (fineName == null && GreenPoleConfig.userManagementProperties != null) {
            fineName = GreenPoleConfig.resolveOptionalEnvFromSystem(
                    GreenPoleConfig.userManagementProperties.getString("app.fine-name", ""));
        }

    }

}
