package org.hkijena.jipipe.launcher.api.runs;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.hkijena.jipipe.api.AbstractJIPipeRunnable;
import org.hkijena.jipipe.extensions.settings.RuntimeSettings;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.launcher.api.boostrap.JIPipeLauncherBoostrapRepo;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstanceDownload;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstanceDownloadResult;
import org.hkijena.jipipe.utils.ArchiveUtils;
import org.hkijena.jipipe.utils.PathUtils;
import org.hkijena.jipipe.utils.WebUtils;
import org.hkijena.jipipe.utils.json.JsonUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class UpdateInstallerRun extends AbstractJIPipeRunnable {
    @Override
    public String getTaskLabel() {
        return "Update installer";
    }

    @Override
    public void run() {
        getProgressInfo().setLogToStdOut(true);
        JIPipeLauncherBoostrapRepo repo;
        try {
            Path tmpFile = RuntimeSettings.generateTempFile("JIPipeLauncherBoostrap", ".json");
            WebUtils.downloadNative(new URL(JIPipeLauncherCommons.getInstance().getSettings().getBoostrapRepositoryUrl()), tmpFile, "Query repository", getProgressInfo());
            repo = JsonUtils.readFromFile(tmpFile, JIPipeLauncherBoostrapRepo.class);
            Files.delete(tmpFile);
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }

        JIPipeInstanceDownload installerDownload = repo.getInstallerDownload();

        if(installerDownload == null) {
            getProgressInfo().log("--> No installer found!");
            return;
        }

        boolean needsUpdate = false;
        if(!Files.exists(JIPipeLauncherCommons.getInstance().getInstallerPath()) || !Files.isRegularFile(JIPipeLauncherCommons.getInstance().getInstallerSha1Path())) {
            getProgressInfo().log(JIPipeLauncherCommons.getInstance().getInstallerPath() + " does not exist / not tagged with SHA1 -> download installer");
            needsUpdate = true;
        }
        else {
            try {
                // Check SHA
                String currentSha1 = new String(Files.readAllBytes(JIPipeLauncherCommons.getInstance().getInstallerSha1Path()), StandardCharsets.UTF_8);
                String wantedSha1 = installerDownload.getSha1();

                if(!Objects.equals(currentSha1, wantedSha1)) {
                    getProgressInfo().log("SHA1 difference '" + currentSha1 + "' <> '" + wantedSha1 + "'");
                    needsUpdate = true;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                getProgressInfo().log("Error while checking SHA1");
                needsUpdate = true;
            }
        }

        if(!needsUpdate) {
            getProgressInfo().log("No update required");
            return;
        }

        // Create directories
        PathUtils.ensureParentDirectoriesExist(JIPipeLauncherCommons.getInstance().getInstallerPath());

        if(SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_LINUX) {
            JIPipeInstanceDownloadResult downloadResult = installerDownload.download(getProgressInfo());

            // Delete old installer
            try {
                Files.deleteIfExists(JIPipeLauncherCommons.getInstance().getInstallerPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Copy new installer
            try {
                Files.copy(downloadResult.getOutputFile(), JIPipeLauncherCommons.getInstance().getInstallerPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Delete downloaded file
            try {
                Files.delete(downloadResult.getOutputFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(SystemUtils.IS_OS_MAC_OSX) {
            JIPipeInstanceDownloadResult downloadResult = installerDownload.download(getProgressInfo());

            // Delete old installer
            if(Files.exists(JIPipeLauncherCommons.getInstance().getInstallerPath())) {
                PathUtils.deleteDirectoryRecursively(JIPipeLauncherCommons.getInstance().getInstallerPath(), getProgressInfo().resolve("Delete old installer"));
            }

            // Extract archive
            try {
                ArchiveUtils.decompressZipFile(downloadResult.getOutputFile(), JIPipeLauncherCommons.getInstance().getInstallerPath().getParent(), getProgressInfo());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Delete downloaded file
            try {
                Files.delete(downloadResult.getOutputFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Write new SHA1
        try {
            Files.write(JIPipeLauncherCommons.getInstance().getInstallerSha1Path(), installerDownload.getSha1().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
