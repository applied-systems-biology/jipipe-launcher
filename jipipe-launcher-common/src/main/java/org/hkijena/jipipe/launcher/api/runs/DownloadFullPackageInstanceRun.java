package org.hkijena.jipipe.launcher.api.runs;

import org.hkijena.jipipe.api.AbstractJIPipeRunnable;
import org.hkijena.jipipe.api.JIPipeProgressInfo;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstance;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstanceDownload;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstanceDownloadResult;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.utils.ArchiveUtils;
import org.hkijena.jipipe.utils.json.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadFullPackageInstanceRun extends AbstractJIPipeRunnable {

    private final JIPipeInstance instance;
    private final JIPipeInstanceDownload download;

    public DownloadFullPackageInstanceRun(JIPipeInstance instance, JIPipeInstanceDownload download) {
        this.instance = instance;
        this.download = download;
    }

    @Override
    public String getTaskLabel() {
        return "Download & install " + instance.getDisplayName() + " / " + download.getName();
    }

    @Override
    public void run() {
        getProgressInfo().setLogToStdOut(true);
        JIPipeProgressInfo progressInfo = getProgressInfo();

        // Download the archive
        progressInfo.log("Downloading archive ...");
        JIPipeInstanceDownloadResult downloadResult = download.download(progressInfo);
        progressInfo.log("Archive extension detected as " + downloadResult.getExtension());

        // Determine target path
        Path absoluteInstallationPath = JIPipeLauncherCommons.getInstance().findNewInstanceDirectory(
                instance.getName().toLowerCase() + "-" + instance.getVersion());

        // Extract archive
        progressInfo.log("Extracting archive ... " + downloadResult.getOutputFile());
        if (downloadResult.getExtension().equals(".zip")) {
            try {
                ArchiveUtils.decompressZipFile(downloadResult.getOutputFile(), absoluteInstallationPath, progressInfo.resolve("Extract package"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                ArchiveUtils.decompressTarGZ(downloadResult.getOutputFile(), absoluteInstallationPath, progressInfo.resolve("Extract package"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Register the instance
        JIPipeInstance newInstance = new JIPipeInstance(instance);
        newInstance.setInstallDirectory(absoluteInstallationPath);
        JIPipeLauncherCommons.getInstance().addInstalledInstance(newInstance);


        // Write a copy of the instance info
        JsonUtils.saveToFile(instance, absoluteInstallationPath.resolve("jipipe-launcher-instance-info.json"));

        // Cleanup
        try {
            Files.delete(downloadResult.getOutputFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public JIPipeInstance getInstance() {
        return instance;
    }

    public JIPipeInstanceDownload getDownload() {
        return download;
    }
}
