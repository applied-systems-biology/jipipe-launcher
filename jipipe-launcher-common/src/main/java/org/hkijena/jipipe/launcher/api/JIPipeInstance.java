package org.hkijena.jipipe.launcher.api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.commons.exec.*;
import org.apache.commons.lang3.SystemUtils;
import org.hkijena.jipipe.extensions.parameters.library.images.ImageParameter;
import org.hkijena.jipipe.utils.PathUtils;
import org.hkijena.jipipe.utils.ProcessUtils;
import org.hkijena.jipipe.utils.StringUtils;
import org.hkijena.jipipe.utils.UIUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JIPipeInstance {
    private String name;
    private String version;

    private String branch;
    private Path installDirectory;
    private List<JIPipeInstanceDownload> downloads = new ArrayList<>();

    public JIPipeInstance() {
    }

    public JIPipeInstance(JIPipeInstance other) {
        this.name = other.name;
        this.version = other.version;
        this.branch = other.branch;
        this.installDirectory = other.installDirectory;
        for (JIPipeInstanceDownload download : other.downloads) {
            this.downloads.add(new JIPipeInstanceDownload(download));
        }
    }

    @JsonGetter("downloads")
    public List<JIPipeInstanceDownload> getDownloads() {
        return downloads;
    }

    @JsonSetter("downloads")
    public void setDownloads(List<JIPipeInstanceDownload> downloads) {
        this.downloads = downloads;
    }

    @JsonGetter("branch")
    public String getBranch() {
        return branch;
    }

    @JsonSetter("branch")
    public void setBranch(String branch) {
        this.branch = branch;
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

    public List<JIPipeInstanceDownload> getCompatibleDownloads() {
        List<JIPipeInstanceDownload> result = new ArrayList<>();
        for (JIPipeInstanceDownload download : downloads) {
            if(download.getType() == JIPipeInstanceDownloadType.FullPackage) {
                String currentOS;
                if(SystemUtils.IS_OS_WINDOWS) {
                    currentOS = "windows";
                }
                else if(SystemUtils.IS_OS_LINUX) {
                    currentOS = "linux";
                }
                else if(SystemUtils.IS_OS_MAC_OSX) {
                    currentOS = "macos";
                }
                else {
                    continue;
                }
                if(download.getOperatingSystems().isEmpty() || download.getOperatingSystems().contains(currentOS) ) {
                    result.add(download);
                }
            }
            else if(download.getType() == JIPipeInstanceDownloadType.JAR) {
                result.add(download);
            }
        }
        return result;
    }

    public Path getImageJExecutablePath() {
        if(SystemUtils.IS_OS_WINDOWS) {
            return installDirectory.resolve("Fiji.app").resolve("ImageJ-win64.exe");
        }
        else if(SystemUtils.IS_OS_LINUX) {
            return installDirectory.resolve("Fiji.app").resolve("ImageJ-linux64");
        }
        else {
            throw new UnsupportedOperationException("Unsupported operating system!");
        }
    }

    public Path getImageJWorkDirectoryPath() {
        return installDirectory.resolve("Fiji.app");
    }

    public void startImageJ(ExecuteResultHandler handler) {
        CommandLine commandLine = new CommandLine(getImageJExecutablePath().toFile());
        startCommandLine(handler, commandLine);
    }

    private void startCommandLine(ExecuteResultHandler handler, CommandLine commandLine) {
        DefaultExecutor executor = new DefaultExecutor();

        try {
            if(!SystemUtils.IS_OS_WINDOWS) {
                PathUtils.makeUnixExecutable(getImageJExecutablePath());
            }
            executor.setWorkingDirectory(getImageJWorkDirectoryPath().toFile());
            executor.execute(commandLine, handler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startJIPipe(ExecuteResultHandler handler) {
        CommandLine commandLine = new CommandLine(getImageJExecutablePath().toFile());
        commandLine
                .addArgument("--pass-classpath")
                .addArgument("--full-classpath")
                .addArgument("--main-class")
                .addArgument("org.hkijena.jipipe.JIPipeLauncher");
        startCommandLine(handler, commandLine);
    }
}
