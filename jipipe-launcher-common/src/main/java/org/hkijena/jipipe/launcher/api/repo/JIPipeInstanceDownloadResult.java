package org.hkijena.jipipe.launcher.api.repo;

import java.nio.file.Path;

public class JIPipeInstanceDownloadResult {
    private final Path outputFile;
    private final String extension;

    public JIPipeInstanceDownloadResult(Path outputFile, String extension) {

        this.outputFile = outputFile;
        this.extension = extension;
    }

    public Path getOutputFile() {
        return outputFile;
    }

    public String getExtension() {
        return extension;
    }
}
