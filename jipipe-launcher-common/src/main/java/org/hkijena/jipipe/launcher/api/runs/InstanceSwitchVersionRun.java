package org.hkijena.jipipe.launcher.api.runs;

import org.hkijena.jipipe.api.AbstractJIPipeRunnable;
import org.hkijena.jipipe.extensions.settings.RuntimeSettings;
import org.hkijena.jipipe.launcher.api.JIPipeInstance;
import org.hkijena.jipipe.launcher.api.JIPipeInstanceDownload;
import org.hkijena.jipipe.launcher.api.JIPipeInstanceDownloadResult;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.launcher.api.events.InstancesUpdatedEvent;
import org.hkijena.jipipe.utils.ArchiveUtils;
import org.hkijena.jipipe.utils.PathUtils;
import org.hkijena.jipipe.utils.WebUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InstanceSwitchVersionRun extends AbstractJIPipeRunnable {
    private final JIPipeInstance instance;
    private final JIPipeInstanceDownload download;

    public InstanceSwitchVersionRun(JIPipeInstance instance, JIPipeInstanceDownload download) {
        this.instance = instance;
        this.download = download;
    }

    @Override
    public String getTaskLabel() {
        return "Switch version";
    }

    @Override
    public void run() {
        Path pluginDir = instance.getAbsoluteApplicationDirectory().resolve("plugins").resolve("JIPipe");
        Path jarDir = instance.getAbsoluteApplicationDirectory().resolve("jars");

        // Download and extract
        JIPipeInstanceDownloadResult downloadResult = download.download(getProgressInfo());
        Path tmpDir = RuntimeSettings.generateTempDirectory("JIPipe-Launcher-Update");
        if (downloadResult.getExtension().equals(".zip")) {
            try {
                ArchiveUtils.decompressZipFile(downloadResult.getOutputFile(), tmpDir, getProgressInfo().resolve("Extract package"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                ArchiveUtils.decompressTarGZ(downloadResult.getOutputFile(), tmpDir, getProgressInfo().resolve("Extract package"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
        instance.setVersion(download.getVersion());
        JIPipeLauncherCommons.getInstance().writeSettings();
        JIPipeLauncherCommons.getInstance().getInstancesUpdatedEventEmitter()
                .emit(new InstancesUpdatedEvent(JIPipeLauncherCommons.getInstance()));

    }
}
