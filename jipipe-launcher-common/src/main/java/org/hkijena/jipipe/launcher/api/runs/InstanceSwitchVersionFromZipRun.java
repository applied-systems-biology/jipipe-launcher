package org.hkijena.jipipe.launcher.api.runs;

import org.hkijena.jipipe.api.AbstractJIPipeRunnable;
import org.hkijena.jipipe.extensions.settings.RuntimeSettings;
import org.hkijena.jipipe.launcher.api.*;
import org.hkijena.jipipe.launcher.api.events.InstancesUpdatedEvent;
import org.hkijena.jipipe.utils.ArchiveUtils;
import org.hkijena.jipipe.utils.PathUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InstanceSwitchVersionFromZipRun extends AbstractJIPipeRunnable {
    private final JIPipeInstance targetInstance;
    private final Path zipFile;

    public InstanceSwitchVersionFromZipRun(JIPipeInstance targetInstance, Path zipFile) {
        this.targetInstance = targetInstance;
        this.zipFile = zipFile;
    }

    @Override
    public String getTaskLabel() {
        return "Switch version (ZIP)";
    }

    @Override
    public void run() {
        Path pluginDir = targetInstance.getAbsoluteApplicationDirectory().resolve("plugins").resolve("JIPipe");
        Path jarDir = targetInstance.getAbsoluteApplicationDirectory().resolve("jars");

        // Download and extract
        Path tmpDir = RuntimeSettings.generateTempDirectory("JIPipe-Launcher-Update");
        try {
            ArchiveUtils.decompressZipFile(zipFile, tmpDir, getProgressInfo().resolve("Extract package"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Check ZIP file
        if(PathUtils.findFilesByExtensionIn(tmpDir, ".jar").isEmpty()) {
            throw new RuntimeException("Invalid archive contents!");
        }
        if(!Files.isDirectory(tmpDir.resolve("dependencies"))) {
            throw new RuntimeException("Invalid archive contents!");
        }

        // Delete the plugin
        PathUtils.deleteDirectoryRecursively(pluginDir, getProgressInfo().resolve("Delete old plugin"));
        try {
            Files.createDirectories(pluginDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Copy the plugins
        for (Path sourceFile : PathUtils.findFilesByExtensionIn(tmpDir, ".jar")) {
            Path targetFile = pluginDir.resolve(sourceFile.getFileName());
            getProgressInfo().log(sourceFile + " --> " + targetFile);
            try {
                Files.copy(sourceFile, targetFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Copy dependencies
        for (Path sourceFile : PathUtils.findFilesByExtensionIn(tmpDir.resolve("dependencies"), ".jar")) {
            Path targetFile = jarDir.resolve(sourceFile.getFileName());
            if(!Files.exists(targetFile)) {
                getProgressInfo().log(sourceFile + " --> " + targetFile);
                try {
                    Files.copy(sourceFile, targetFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // Update the instance
        targetInstance.autoDetectVersion();
        targetInstance.setChangeLog(JIPipeLauncherCommons.getInstance().findChangeLog(targetInstance.getVersion()));
        JIPipeLauncherCommons.getInstance().writeSettings();
        JIPipeLauncherCommons.getInstance().getInstancesUpdatedEventEmitter()
                .emit(new InstancesUpdatedEvent(JIPipeLauncherCommons.getInstance()));

        // Cleanup
        PathUtils.deleteDirectoryRecursively(tmpDir, getProgressInfo().resolve("Cleanup"));

    }
}
