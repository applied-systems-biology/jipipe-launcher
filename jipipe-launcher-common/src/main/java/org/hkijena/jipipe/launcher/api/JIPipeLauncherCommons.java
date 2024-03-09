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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JIPipeLauncherCommons implements JIPipeRunnable.FinishedEventListener {
    private static JIPipeLauncherCommons INSTANCE;

    private Path settingsPath;
    private JIPipeLauncherSettings settings = new JIPipeLauncherSettings();
    private final List<JIPipeInstance> availableInstances = new ArrayList<>();

    private final InstancesUpdatedEventEmitter instancesUpdatedEventEmitter = new InstancesUpdatedEventEmitter();

    public JIPipeLauncherCommons() {
        JIPipeRunnerQueue.getInstance().getFinishedEventEmitter().subscribe(this);
    }

    public void initialize() {
        initializeSettings();
        initializeInstanceDirectory();
    }

    private void initializeInstanceDirectory() {
        Path instancePath = settings.getDefaultInstanceDirectory();
        if(StringUtils.isNullOrEmpty(instancePath) || !Files.isDirectory(instancePath)) {
            if(SystemUtils.IS_OS_WINDOWS) {
                instancePath = Paths.get(System.getenv("APPDATA")).resolve("JIPipe").resolve("instances");
            }
            else {
                if(System.getProperties().containsKey("XDG_DATA_HOME") && !StringUtils.isNullOrEmpty(System.getProperty("XDG_DATA_HOME"))) {
                    instancePath = Paths.get(System.getProperty("XDG_DATA_HOME")).resolve(".local")
                            .resolve("share").resolve("JIPipe").resolve("instances");
                }
                else {
                    instancePath = Paths.get(System.getProperty("user.home")).resolve(".local")
                            .resolve("share").resolve("JIPipe").resolve("instances");
                }
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
        if(SystemUtils.IS_OS_WINDOWS) {
            settingsPath = Paths.get(System.getenv("APPDATA")).resolve("JIPipe").resolve("launcher-settings.json");
        }
        else {
            if(System.getProperties().containsKey("XDG_CONFIG_HOME") && !StringUtils.isNullOrEmpty(System.getProperty("XDG_CONFIG_HOME"))) {
                settingsPath = Paths.get(System.getProperty("XDG_CONFIG_HOME")).resolve(".config").resolve("JIPipe").resolve("launcher-settings.json");
            }
            else {
                settingsPath = Paths.get(System.getProperty("user.home")).resolve(".config").resolve("JIPipe").resolve("launcher-settings.json");
            }
        }
        try {
            Files.createDirectories(settingsPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Load existing settings
        if(Files.exists(settingsPath)) {
            try {
                settings = JsonUtils.readFromFile(settingsPath, JIPipeLauncherSettings.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(settings == null) {
                settings = new JIPipeLauncherSettings();
                writeSettings();
            }
        }
        else {
            writeSettings();
        }
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
        if(INSTANCE == null) {
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

    public List<JIPipeInstance> getSortedInstanceList() {
        List<JIPipeInstance> instances = new ArrayList<>(availableInstances);
        instances.addAll(settings.getInstalledInstances());
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
        if(event.getRun() instanceof QueryAvailableInstancesRun) {
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
}
