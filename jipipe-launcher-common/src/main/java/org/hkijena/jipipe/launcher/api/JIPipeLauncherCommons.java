package org.hkijena.jipipe.launcher.api;

import org.apache.commons.lang3.SystemUtils;
import org.hkijena.jipipe.api.JIPipeRunnable;
import org.hkijena.jipipe.launcher.api.events.InstancesUpdatedEvent;
import org.hkijena.jipipe.launcher.api.events.InstancesUpdatedEventEmitter;
import org.hkijena.jipipe.launcher.api.runs.QueryAvailableInstancesRun;
import org.hkijena.jipipe.ui.running.JIPipeRunnerQueue;
import org.hkijena.jipipe.utils.StringUtils;
import org.hkijena.jipipe.utils.json.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class JIPipeLauncherCommons implements JIPipeRunnable.FinishedEventListener {
    private static JIPipeLauncherCommons INSTANCE;

    private Path settingsPath;
    private Path installerPath;
    private Path launcherPath;
    private JIPipeLauncherSettings settings = new JIPipeLauncherSettings();
    private final List<JIPipeInstance> availableInstances = new ArrayList<>();

    private final InstancesUpdatedEventEmitter instancesUpdatedEventEmitter = new InstancesUpdatedEventEmitter();

    public JIPipeLauncherCommons() {
        JIPipeRunnerQueue.getInstance().getFinishedEventEmitter().subscribe(this);
    }

    public void initialize() {
        initializeSettings();
        initializeInstanceDirectory();
        initializeBootstrapPaths();
    }

    private void initializeBootstrapPaths() {
        if (SystemUtils.IS_OS_WINDOWS) {
            launcherPath = Paths.get(System.getenv("APPDATA")).resolve("JIPipe")
                    .resolve("launcher").resolve("jipipe-launcher.exe");
            installerPath = Paths.get(System.getenv("APPDATA")).resolve("JIPipe")
                    .resolve("launcher").resolve("JIPipeInstaller.exe");
        } else if (SystemUtils.IS_OS_LINUX) {
            if (System.getProperties().containsKey("XDG_DATA_HOME") && !StringUtils.isNullOrEmpty(System.getProperty("XDG_DATA_HOME"))) {
                launcherPath = Paths.get(System.getProperty("XDG_DATA_HOME")).resolve(".local")
                        .resolve("share").resolve("JIPipe")
                        .resolve("launcher").resolve("jipipe-launcher.AppImage");
                installerPath = Paths.get(System.getProperty("XDG_DATA_HOME")).resolve(".local")
                        .resolve("share").resolve("JIPipe")
                        .resolve("launcher").resolve("JIPipeInstaller.AppImage");
            } else {
                launcherPath = Paths.get(System.getProperty("user.home")).resolve(".local")
                        .resolve("share").resolve("JIPipe")
                        .resolve("launcher").resolve("jipipe-launcher.AppImage");
                installerPath = Paths.get(System.getProperty("user.home")).resolve(".local")
                        .resolve("share").resolve("JIPipe")
                        .resolve("launcher").resolve("JIPipeInstaller.AppImage");
            }
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            launcherPath = Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support")
                    .resolve("JIPipe Launcher").resolve("launcher").resolve("jipipe-launcher.App");
            installerPath = Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support")
                    .resolve("JIPipe Launcher").resolve("launcher").resolve("JIPipe.App");
        } else {
            throw new UnsupportedOperationException("Unknown operating system!");
        }
    }

    private void initializeInstanceDirectory() {
        Path instancePath = settings.getDefaultInstanceDirectory();
        if (StringUtils.isNullOrEmpty(instancePath) || !Files.isDirectory(instancePath)) {
            if (SystemUtils.IS_OS_WINDOWS) {
                instancePath = Paths.get(System.getenv("APPDATA")).resolve("JIPipe").resolve("instances");
            } else if (SystemUtils.IS_OS_LINUX) {
                if (System.getProperties().containsKey("XDG_DATA_HOME") && !StringUtils.isNullOrEmpty(System.getProperty("XDG_DATA_HOME"))) {
                    instancePath = Paths.get(System.getProperty("XDG_DATA_HOME")).resolve(".local")
                            .resolve("share").resolve("JIPipe").resolve("instances");
                } else {
                    instancePath = Paths.get(System.getProperty("user.home")).resolve(".local")
                            .resolve("share").resolve("JIPipe").resolve("instances");
                }
            } else if (SystemUtils.IS_OS_MAC_OSX) {
                instancePath = Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Application Support")
                        .resolve("JIPipe Launcher").resolve("instances");
            } else {
                throw new UnsupportedOperationException("Unknown operating system!");
            }

            try {
                Files.createDirectories(instancePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            settings.setDefaultInstanceDirectory(instancePath);
            writeSettings();
        }
    }

    private void initializeSettings() {
        if (SystemUtils.IS_OS_WINDOWS) {
            settingsPath = Paths.get(System.getenv("APPDATA")).resolve("JIPipe").resolve("launcher-settings.json");
        } else if (SystemUtils.IS_OS_LINUX) {
            if (System.getProperties().containsKey("XDG_CONFIG_HOME") && !StringUtils.isNullOrEmpty(System.getProperty("XDG_CONFIG_HOME"))) {
                settingsPath = Paths.get(System.getProperty("XDG_CONFIG_HOME")).resolve(".config").resolve("JIPipe").resolve("launcher-settings.json");
            } else {
                settingsPath = Paths.get(System.getProperty("user.home")).resolve(".config").resolve("JIPipe").resolve("launcher-settings.json");
            }
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            settingsPath = Paths.get(System.getProperty("user.home")).resolve("Library").resolve("Preferences")
                    .resolve("JIPipe Launcher").resolve("launcher-settings.json");
        } else {
            throw new UnsupportedOperationException("Unknown operating system!");
        }

        try {
            Files.createDirectories(settingsPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Load existing settings
        if (Files.exists(settingsPath)) {
            try {
                settings = JsonUtils.readFromFile(settingsPath, JIPipeLauncherSettings.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (settings == null) {
                settings = new JIPipeLauncherSettings();
                writeSettings();
            }
        } else {
            writeSettings();
        }

        // Delete missing instances
        removeMissingInstances();
    }

    private void removeMissingInstances() {
        Set<JIPipeInstance> toDelete = new HashSet<>();
        for (JIPipeInstance installedInstance : getSettings().getInstalledInstances()) {
            if (!Files.isDirectory(installedInstance.getInstallDirectory())) {
                toDelete.add(installedInstance);
            }
        }
        for (JIPipeInstance instance : toDelete) {
            getSettings().getInstalledInstances().remove(instance);
        }
        if (!toDelete.isEmpty()) {
            writeSettings();
        }
    }

    public Path getInstallerPath() {
        return installerPath;
    }

    public Path getLauncherPath() {
        return launcherPath;
    }

    public Path getSettingsPath() {
        return settingsPath;
    }

    public JIPipeLauncherSettings getSettings() {
        return settings;
    }

    public InstancesUpdatedEventEmitter getInstancesUpdatedEventEmitter() {
        return instancesUpdatedEventEmitter;
    }

    public void writeSettings() {
        JsonUtils.saveToFile(settings, settingsPath);
    }

    public static JIPipeLauncherCommons getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JIPipeLauncherCommons();
        }
        return INSTANCE;
    }

    public void setSetting(String key, Object value) {
        settings.setParameter(key, value);
        writeSettings();
    }

    public void queryAvailableInstances() {
        JIPipeRunnerQueue.getInstance().enqueue(new QueryAvailableInstancesRun());
    }

    public List<JIPipeInstance> getSortedAvailableInstanceList() {
        List<JIPipeInstance> instances = new ArrayList<>(availableInstances);
        instances.sort(Comparator.comparing(JIPipeInstance::isInstalled).reversed()
                .thenComparing(JIPipeInstance::getVersion, new VersionComparator().reversed())
                .thenComparing(JIPipeInstance::getDisplayName));
        return instances;
    }

    public List<JIPipeInstance> getSortedInstanceList() {
        List<JIPipeInstance> instances = new ArrayList<>(settings.getInstalledInstances());
        for (JIPipeInstance availableInstance : availableInstances) {
            // Annotate instances with the same version with the URL
            List<JIPipeInstance> installedWithVersion = settings.getInstalledInstances().stream().filter(installed -> Objects.equals(availableInstance.getVersion(),
                    installed.getVersion())).collect(Collectors.toList());
            for (JIPipeInstance instance : installedWithVersion) {
                instance.setDownloads(availableInstance.getDownloads());
                instance.setChangeLog(availableInstance.getChangeLog());
            }
            if (installedWithVersion.isEmpty()) {
                // Not installed --> add
                instances.add(availableInstance);
            }
        }
        instances.sort(Comparator.comparing(JIPipeInstance::isInstalled).reversed()
                .thenComparing(JIPipeInstance::getVersion, new VersionComparator().reversed())
                .thenComparing(JIPipeInstance::getDisplayName));
        return instances;
    }

    public JIPipeInstance findLatestInstalledInstance() {
        return getSortedInstanceList().stream().filter(JIPipeInstance::isInstalled).findFirst().orElse(null);
    }

    public JIPipeInstance findLatestAvailableInstance() {
        return getSortedInstanceList().stream().filter(JIPipeInstance::isNotInstalled).findFirst().orElse(null);
    }

    @Override
    public void onRunnableFinished(JIPipeRunnable.FinishedEvent event) {
        if (event.getRun() instanceof QueryAvailableInstancesRun) {
            this.availableInstances.clear();
            this.availableInstances.addAll(((QueryAvailableInstancesRun) event.getRun()).getInstances());
            instancesUpdatedEventEmitter.emit(new InstancesUpdatedEvent(this));
        }
    }

    public void addInstalledInstance(JIPipeInstance newInstance) {
        getSettings().getInstalledInstances().add(newInstance);
        writeSettings();
        instancesUpdatedEventEmitter.emit(new InstancesUpdatedEvent(this));
    }

    public Path findNewInstanceDirectory(String name) {
        name = StringUtils.makeUniqueString(name, "-",
                s -> Files.exists(getSettings().getDefaultInstanceDirectory().resolve(s)));
        return getSettings().getDefaultInstanceDirectory().resolve(name);
    }

    public void labelInstalledInstance(JIPipeInstance instance, String newName) {
        if (!instance.isInstalled()) {
            throw new IllegalArgumentException("Instance not installed");
        }

        // Try to rename the directory if this is not already customized
        try {
            if (!instance.isCustomized()) {
                if (installedInstanceLocatedInDefaultDirectory(instance)) {
                    String newDirectoryName = StringUtils.safeJsonify(newName.trim().toLowerCase());
                    if (newDirectoryName.length() > 16) {
                        newDirectoryName = newDirectoryName.substring(0, 16);
                    }

                    Path newDirectory = findNewInstanceDirectory(instance.getInstallDirectory().getFileName() + "-" + newDirectoryName);
                    Files.move(instance.getInstallDirectory(), newDirectory);
                    instance.setInstallDirectory(newDirectory);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        instance.setCustomized(true);
        instance.setName(newName);
        writeSettings();

        instancesUpdatedEventEmitter.emit(new InstancesUpdatedEvent(this));
    }

    public boolean installedInstanceLocatedInDefaultDirectory(JIPipeInstance instance) {
        return instance.isInstalled() && instance.getInstallDirectory().startsWith(getSettings().getDefaultInstanceDirectory());
    }

    public void removeInstance(JIPipeInstance instance) {
        getSettings().getInstalledInstances().remove(instance);
        writeSettings();
        instancesUpdatedEventEmitter.emit(new InstancesUpdatedEvent(this));
    }

    public String importInstance(Path directory, String name) {
        if (getSettings().getInstalledInstances().stream()
                .anyMatch(instance -> instance.getAbsoluteApplicationDirectory().equals(directory.toAbsolutePath()))) {
            return "Selected instance is already imported!";
        }

        JIPipeInstance instance = new JIPipeInstance();
        instance.setName(name);
        instance.setCustomized(true);
        instance.setInstallDirectory(directory);
        instance.setApplicationDirectory(Paths.get(""));

        if (!Files.isRegularFile(instance.getAbsoluteExecutablePath())) {
            return "Could not find executable! Select the ImageJ application directory.";
        }

        instance.autoDetectVersion();

        // Register the instance
        getSettings().getInstalledInstances().add(instance);
        writeSettings();

        instancesUpdatedEventEmitter.emit(new InstancesUpdatedEvent(this));
        return null;
    }

    public JIPipeInstanceChangeLog findChangeLog(String version) {
        for (JIPipeInstance availableInstance : availableInstances) {
            if (Objects.equals(availableInstance.getVersion(), version)) {
                return availableInstance.getChangeLog();
            }
        }
        return null;
    }

    public JIPipeInstanceDownload findUpdate(JIPipeInstance instance, boolean includeUnstable) {
        JIPipeInstanceDownload result = null;
        String resultVersion = instance.getVersion();
        for (JIPipeInstance availableInstance : availableInstances) {
            if (!includeUnstable && !availableInstance.isStable()) {
                continue;
            }
            if (StringUtils.compareVersions(resultVersion, availableInstance.getVersion()) < 0) {
                List<JIPipeInstanceDownload> compatibleDownloads = availableInstance.getCompatibleDownloads(JIPipeInstanceDownloadType.JAR);
                if (!compatibleDownloads.isEmpty()) {
                    result = compatibleDownloads.get(0);
                    resultVersion = availableInstance.getVersion();
                }
            }
        }
        return result;
    }
}
