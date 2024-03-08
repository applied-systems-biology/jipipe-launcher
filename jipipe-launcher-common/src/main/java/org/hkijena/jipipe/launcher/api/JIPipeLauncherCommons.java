package org.hkijena.jipipe.launcher.api;

import org.apache.commons.lang3.SystemUtils;
import org.hkijena.jipipe.api.JIPipeRunnable;
import org.hkijena.jipipe.launcher.api.runs.UpdateAvailableInstancesRun;
import org.hkijena.jipipe.ui.running.JIPipeRunnerQueue;
import org.hkijena.jipipe.utils.StringUtils;
import org.hkijena.jipipe.utils.json.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JIPipeLauncherCommons implements JIPipeRunnable.FinishedEventListener {
    private static JIPipeLauncherCommons INSTANCE;

    private Path settingsPath;
    private JIPipeLauncherSettings settings = new JIPipeLauncherSettings();
    private final JIPipeInstanceRepository repository = new JIPipeInstanceRepository();

    public JIPipeLauncherCommons() {
        JIPipeRunnerQueue.getInstance().getFinishedEventEmitter().subscribe(this);
    }

    public void initialize() {
        initializeSettings();
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

    public void writeSettings() {
        JsonUtils.saveToFile(settings, settingsPath);
    }

    public JIPipeInstanceRepository getRepository() {
        return repository;
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

    public void updateRepository() {
        JIPipeRunnerQueue.getInstance().enqueue(new UpdateAvailableInstancesRun());
    }

    @Override
    public void onRunnableFinished(JIPipeRunnable.FinishedEvent event) {
        if(event.getRun() instanceof UpdateAvailableInstancesRun) {
            repository.update(((UpdateAvailableInstancesRun) event.getRun()).getInstances());
        }
    }
}
