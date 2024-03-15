package org.hkijena.jipipe.launcher.api.runs;

import org.hkijena.jipipe.api.AbstractJIPipeRunnable;
import org.hkijena.jipipe.api.JIPipeProgressInfo;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstance;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstanceDownload;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstanceDownloadResult;
import org.hkijena.jipipe.utils.ArchiveUtils;
import org.hkijena.jipipe.utils.json.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InstallFullPackageZipRun extends AbstractJIPipeRunnable {

    private final Path zipFile;
    private final String customName;

    public InstallFullPackageZipRun(Path zipFile, String customName) {
        this.zipFile = zipFile;
        this.customName = customName;
    }

    @Override
    public String getTaskLabel() {
        return "Install " + zipFile.getFileName();
    }

    @Override
    public void run() {
        getProgressInfo().setLogToStdOut(true);
        JIPipeProgressInfo progressInfo = getProgressInfo();



        // Determine target path
        Path absoluteInstallationPath = JIPipeLauncherCommons.getInstance().findNewInstanceDirectory(
                customName.toLowerCase() + "-zip");

        // Extract archive
        progressInfo.log("Extracting archive ... " + zipFile);
        if (zipFile.getFileName().toString().endsWith(".zip")) {
            try {
                ArchiveUtils.decompressZipFile(zipFile, absoluteInstallationPath, progressInfo.resolve("Extract package"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                ArchiveUtils.decompressTarGZ(zipFile, absoluteInstallationPath, progressInfo.resolve("Extract package"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Register the instance
        JIPipeInstance newInstance = new JIPipeInstance();
        newInstance.setCustomized(true);
        newInstance.setName(customName);
        newInstance.setInstallDirectory(absoluteInstallationPath);
        newInstance.autoDetectVersion();
        JIPipeLauncherCommons.getInstance().addInstalledInstance(newInstance);

        // Write a copy of the instance info
        JsonUtils.saveToFile(newInstance, absoluteInstallationPath.resolve("jipipe-launcher-instance-info.json"));
    }

    public Path getZipFile() {
        return zipFile;
    }

    public String getCustomName() {
        return customName;
    }
}
