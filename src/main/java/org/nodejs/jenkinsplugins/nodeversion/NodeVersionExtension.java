package org.nodejs.jenkinsplugins.nodeversion;

import hudson.Extension;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.model.Action;
import hudson.model.ParametersAction;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitException;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.GitSCMExtension;
import hudson.plugins.git.extensions.GitSCMExtensionDescriptor;
import hudson.remoting.VirtualChannel;
import jenkins.MasterToSlaveFileCallable;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import static org.nodejs.jenkinsplugins.nodeversion.NodeVersionAction.ENV_VAR_NODEJS_VERSION;
import static org.nodejs.jenkinsplugins.nodeversion.NodeVersionAction.ENV_VAR_NODEJS_MAJOR_VERSION;

public class NodeVersionExtension extends GitSCMExtension {
    private static final Logger LOGGER = Logger.getLogger(NodeVersionExtension.class.getName());

    private boolean exportNodejsVersion;

    @DataBoundConstructor
    public NodeVersionExtension() {
    }

    @DataBoundSetter
    public void setExportNodejsVersion(boolean exportNodejsVersion) {
        this.exportNodejsVersion = exportNodejsVersion;
    }

    public boolean isExportNodejsVersion() {
        return exportNodejsVersion;
    }

    @Override
    public void onCheckoutCompleted(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener)
            throws IOException, InterruptedException, GitException {

        if (exportNodejsVersion) {
            String nodejsVersion = parseNodejsVersion(git.getWorkTree());

            if (nodejsVersion != null) {
                String nodejsMajorVersion = nodejsVersion.split("\\.")[0];

                listener.getLogger().println("Exporting environment variable " + ENV_VAR_NODEJS_VERSION + " with Node.js version '" + nodejsVersion + "'");
                listener.getLogger().println("Exporting environment variable " + ENV_VAR_NODEJS_MAJOR_VERSION + " with Node.js major version '" + nodejsMajorVersion + "'");
                build.addAction(new NodeVersionAction(nodejsVersion, nodejsMajorVersion));

                List<ParametersAction> actions = build.getActions(ParametersAction.class);
                List<ParameterValue> values = new Vector<ParameterValue>();
                values.add(new StringParameterValue(ENV_VAR_NODEJS_VERSION, nodejsVersion));
                values.add(new StringParameterValue(ENV_VAR_NODEJS_MAJOR_VERSION, nodejsMajorVersion));
                if (actions.size() == 0) {
                    build.addAction(new ParametersAction(values.get(0), values.get(1)));
                } else {
                    ParametersAction newAction = actions.get(0).createUpdated(values);
                    build.getActions().remove(actions.get(0));
                    build.getActions().add(newAction);
                }
                listener.getLogger().println("Exporting parameter " + ENV_VAR_NODEJS_VERSION + " with Node.js version '" + nodejsVersion + "'");
                listener.getLogger().println("Exporting parameter " + ENV_VAR_NODEJS_MAJOR_VERSION + " with Node.js major version '" + nodejsMajorVersion + "'");
            } else {
                listener.getLogger().println("Could not find Node.js version in this workspace");
            }
        }
    }

    private String parseNodejsVersion(FilePath workspace) {
        try {
            return workspace.act(new MasterToSlaveFileCallable<String>() {
                public String invoke(File f, VirtualChannel channel) {
                    String major = null, minor = null, patch = null;
                    try {
                        BufferedReader in = new BufferedReader(new FileReader(new File(f, "src/node_version.h")));
                        String line;

                        while ((line = in.readLine()) != null) {
                            if (line.indexOf("define NODE_MAJOR_VERSION ") > -1) {
                                major = line.split(" NODE_MAJOR_VERSION ")[1].trim();
                            } else if (line.indexOf("define NODE_MINOR_VERSION ") > -1) {
                                minor = line.split(" NODE_MINOR_VERSION ")[1].trim();
                            } else if (line.indexOf("define NODE_PATCH_VERSION ") > -1) {
                                patch = line.split(" NODE_PATCH_VERSION ")[1].trim();
                            }
                        }

                        in.close();
                    } catch (Exception e) {
                    }
                    if (major != null && minor != null && patch != null) {
                        return major + "." + minor + "." + patch;
                    }

                    return null;
                }
            });
        } catch (Exception e) {
        }

        return null;
    }

    @Extension
    public static class DescriptorImpl extends GitSCMExtensionDescriptor {
        @Override
        public String getDisplayName() {
            return "Extract Node.js versions as parameters";
        }
    }
}