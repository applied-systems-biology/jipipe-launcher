package org.hkijena.jipipe.launcher.api.runs;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hkijena.jipipe.api.AbstractJIPipeRunnable;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstance;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.utils.WebUtils;
import org.hkijena.jipipe.utils.json.JsonUtils;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class QueryAvailableInstancesRun extends AbstractJIPipeRunnable {

    private List<JIPipeInstance> instances;
    @Override
    public String getTaskLabel() {
        return "Query available instances";
    }

    @Override
    public void run() {
        getProgressInfo().setLogToStdOut(true);
        String repositoryURL = JIPipeLauncherCommons.getInstance().getSettings().getRepositoryUrl();
        getProgressInfo().log("Downloading repository from " + repositoryURL);
        getProgressInfo().setProgress(0, 2);

        try {
            Path repositoryJsonFile = Files.createTempFile("jipipe-launcher-repo", ".json");
            WebUtils.downloadNative(new URL(repositoryURL), repositoryJsonFile, "Repo", getProgressInfo());

            getProgressInfo().setProgress(1, 2);
            TypeReference<List<JIPipeInstance>> typeReference = new TypeReference<List<JIPipeInstance>>() {};
           instances = JsonUtils.getObjectMapper().readerFor(typeReference).readValue(repositoryJsonFile.toFile());

            getProgressInfo().setProgress(2, 2);
            Files.delete(repositoryJsonFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<JIPipeInstance> getInstances() {
        return instances;
    }
}
