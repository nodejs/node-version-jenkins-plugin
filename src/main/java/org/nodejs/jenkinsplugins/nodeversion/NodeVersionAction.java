package org.nodejs.jenkinsplugins.nodeversion;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;

/** Exports the message text associated with a git tag used for a build. */
public class NodeVersionAction implements EnvironmentContributingAction {

    /** The name of the environment variable this plugin exports for a git tag message. */
    public static final String ENV_VAR_NODEJS_VERSION = "NODEJS_VERSION";
    public static final String ENV_VAR_NODEJS_MAJOR_VERSION = "NODEJS_MAJOR_VERSION";

    private final String nodejsVersion;
    private final String nodejsMajorVersion;

    public NodeVersionAction(String nodejsVersion, String nodejsMajorVersion) {
        this.nodejsVersion = nodejsVersion;
        this.nodejsMajorVersion = nodejsMajorVersion;
    }

    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
        if (nodejsVersion != null) {
            env.put(ENV_VAR_NODEJS_VERSION, nodejsVersion);
        }
        if (nodejsMajorVersion != null) {
            env.put(ENV_VAR_NODEJS_MAJOR_VERSION, nodejsMajorVersion);
        }
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return null;
    }
}