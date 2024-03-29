package org.hkijena.jipipe.launcher.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hkijena.jipipe.api.SetJIPipeDocumentation;
import org.hkijena.jipipe.api.parameters.AbstractJIPipeParameterCollection;
import org.hkijena.jipipe.api.parameters.JIPipeParameter;
import org.hkijena.jipipe.extensions.parameters.library.primitives.StringParameterSettings;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstance;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JIPipeLauncherSettings extends AbstractJIPipeParameterCollection {
    private boolean offlineMode = false;
    private boolean updateToUnstable = false;
    private boolean createLauncherIcons = true;
    private String repositoryUrl = "https://raw.githubusercontent.com/applied-systems-biology/JIPipe-Repositories/main/launcher/instances.json";
    private String boostrapRepositoryUrl = "https://raw.githubusercontent.com/applied-systems-biology/JIPipe-Repositories/main/launcher/boostrap.json";
    private Path defaultInstanceDirectory;
    private List<JIPipeInstance> installedInstances = new ArrayList<>();

    public JIPipeLauncherSettings() {
    }

    public JIPipeLauncherSettings(JIPipeLauncherSettings other) {
        this.offlineMode = other.offlineMode;
        this.updateToUnstable = other.updateToUnstable;
        this.createLauncherIcons = other.createLauncherIcons;
        this.repositoryUrl = other.repositoryUrl;
        this.boostrapRepositoryUrl = other.boostrapRepositoryUrl;
        this.defaultInstanceDirectory = other.defaultInstanceDirectory;
        this.installedInstances = other.installedInstances;
    }

    public void setTo(JIPipeLauncherSettings other) {
        this.offlineMode = other.offlineMode;
        this.updateToUnstable = other.updateToUnstable;
        this.createLauncherIcons = other.createLauncherIcons;
        this.repositoryUrl = other.repositoryUrl;
        this.boostrapRepositoryUrl = other.boostrapRepositoryUrl;
        this.defaultInstanceDirectory = other.defaultInstanceDirectory;
//        this.installedInstances = other.installedInstances;
    }

    @SetJIPipeDocumentation(name = "Create launcher icons", description = "Windows/Linux: create desktop and application launcher icons")
    @JIPipeParameter("create-launcher-icons")
    @JsonGetter("create-launcher-icons")
    public boolean isCreateLauncherIcons() {
        return createLauncherIcons;
    }

    @JIPipeParameter("create-launcher-icons")
    @JsonSetter("create-launcher-icons")
    public void setCreateLauncherIcons(boolean createLauncherIcons) {
        this.createLauncherIcons = createLauncherIcons;
    }

    @SetJIPipeDocumentation(name = "Repository URL (Launcher)", description = "Points to the repository for the launcher auto-update")
    @JsonGetter("boostrap-repository-url")
    @JIPipeParameter("boostrap-repository-url")
    public String getBoostrapRepositoryUrl() {
        return boostrapRepositoryUrl;
    }

    @JsonSetter("boostrap-repository-url")
    @JIPipeParameter("boostrap-repository-url")
    public void setBoostrapRepositoryUrl(String boostrapRepositoryUrl) {
        this.boostrapRepositoryUrl = boostrapRepositoryUrl;
    }

    @SetJIPipeDocumentation(name = "Update to unstable versions", description = "If enabled, updates are offered for unstable versions")
    @JIPipeParameter("update-to-unstable")
    @JsonGetter("update-to-unstable")
    public boolean isUpdateToUnstable() {
        return updateToUnstable;
    }

    @JIPipeParameter("update-to-unstable")
    @JsonSetter("update-to-unstable")
    public void setUpdateToUnstable(boolean updateToUnstable) {
        this.updateToUnstable = updateToUnstable;
    }

    @JsonGetter("installed-instances")
    public List<JIPipeInstance> getInstalledInstances() {
        return installedInstances;
    }

    @JsonSetter("installed-instances")
    public void setInstalledInstances(List<JIPipeInstance> installedInstances) {
        this.installedInstances = installedInstances;
    }

    @SetJIPipeDocumentation(name = "Default instance directory", description = "The path were newly installed instances are stored")
    @JIPipeParameter("default-instance-directory")
    @JsonGetter("default-instance-directory")
    public Path getDefaultInstanceDirectory() {
        return defaultInstanceDirectory;
    }

    @JIPipeParameter("default-instance-directory")
    @JsonSetter("default-instance-directory")
    public void setDefaultInstanceDirectory(Path defaultInstanceDirectory) {
        this.defaultInstanceDirectory = defaultInstanceDirectory;
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
    @StringParameterSettings(monospace = true)
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
