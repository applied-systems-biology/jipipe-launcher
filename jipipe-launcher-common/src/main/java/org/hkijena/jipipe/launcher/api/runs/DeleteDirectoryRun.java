package org.hkijena.jipipe.launcher.api.runs;

import org.hkijena.jipipe.api.AbstractJIPipeRunnable;
import org.hkijena.jipipe.utils.PathUtils;

import java.nio.file.Path;

public class DeleteDirectoryRun extends AbstractJIPipeRunnable {
    private final Path directory;

    public DeleteDirectoryRun(Path directory) {
        this.directory = directory;
    }

    @Override
    public String getTaskLabel() {
        return "Delete directory";
    }

    @Override
    public void run() {
        PathUtils.deleteDirectoryRecursively(directory, getProgressInfo());
    }
}
