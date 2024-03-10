package org.hkijena.jipipe.launcher.api.runs;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.hkijena.jipipe.api.AbstractJIPipeRunnable;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.utils.PathUtils;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DuplicateInstanceRun extends AbstractJIPipeRunnable {
    private final Path sourceDirectory;
    private final Path targetDirectory;
    private final String name;

    public DuplicateInstanceRun(Path sourceDirectory, Path targetDirectory, String name) {
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.name = name;
    }

    @Override
    public String getTaskLabel() {
        return "Duplicate instance";
    }

    @Override
    public void run() {
        getProgressInfo().log("Duplicating " + sourceDirectory + " ---> " + targetDirectory);
        if(Files.exists(targetDirectory)) {
            PathUtils.deleteDirectoryRecursively(targetDirectory, getProgressInfo().resolve("Ensuring empty target directory"));
        }
        PathUtils.copyDirectory(sourceDirectory, targetDirectory, getProgressInfo());

        JIPipeLauncherCommons.getInstance().importInstance(targetDirectory, name);
    }
}
