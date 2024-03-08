package org.hkijena.jipipe.launcher.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.hkijena.jipipe.extensions.parameters.library.images.ImageParameter;
import org.hkijena.jipipe.utils.StringUtils;
import org.hkijena.jipipe.utils.UIUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public class JIPipeInstance {
    private String name;
    private String version;
    private Path installDirectory;

    public JIPipeInstance() {
    }

    @JsonGetter("name")
    public String getName() {
        return name;
    }

    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonGetter("version")
    public String getVersion() {
        return version;
    }

    @JsonSetter("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonGetter("install-directory")
    public Path getInstallDirectory() {
        return installDirectory;
    }

    @JsonSetter("install-directory")
    public void setInstallDirectory(Path installDirectory) {
        this.installDirectory = installDirectory;
    }

    public boolean isInstalled() {
        return !StringUtils.isNullOrEmpty(installDirectory) && Files.isDirectory(installDirectory);
    }

    public boolean isNotInstalled() {
        return !isInstalled();
    }

    public String getDisplayName() {
        return StringUtils.orElse(name, "JIPipe") + " " + version;
    }
}
