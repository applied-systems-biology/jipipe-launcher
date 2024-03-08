package org.hkijena.jipipe.launcher.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hkijena.jipipe.api.SetJIPipeDocumentation;
import org.hkijena.jipipe.api.parameters.AbstractJIPipeParameterCollection;
import org.hkijena.jipipe.api.parameters.JIPipeParameter;

public class JIPipeLauncherSettings extends AbstractJIPipeParameterCollection {
    private boolean offlineMode = false;
    private String repositoryUrl = "https://raw.githubusercontent.com/applied-systems-biology/JIPipe-Repositories/main/launcher/instances.json";

    public JIPipeLauncherSettings() {
    }

    public JIPipeLauncherSettings(JIPipeLauncherSettings other) {
        setTo(other);
    }

    public void setTo(JIPipeLauncherSettings other) {
        this.offlineMode = other.offlineMode;
    }

    @SetJIPipeDocumentation(name = "Offline mode", description = "If enabled, avoid contacting remote repositories as much as possible.")
    @JIPipeParameter("offline-mode")
    @JsonGetter("offline-mode")
    public boolean isOfflineMode() {
        return offlineMode;
    }

    @JIPipeParameter("offline-mode")
    @JsonSetter("offline-mode")
    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
    }

    @SetJIPipeDocumentation(name = "Repository URL", description = "Points to the repository of available JIPipe versions")
    @JIPipeParameter("repository-url")
    @JsonGetter("repository-url")
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    @JIPipeParameter("repository-url")
    @JsonSetter("repository-url")
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }
}
