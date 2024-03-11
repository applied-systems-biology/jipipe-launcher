package org.hkijena.jipipe.launcher.api.boostrap;

import org.hkijena.jipipe.launcher.api.repo.JIPipeInstanceDownload;

import java.util.HashMap;

public class JIPipeLauncherBoostrapRepo extends HashMap<String, JIPipeInstanceDownload> {
    public JIPipeInstanceDownload getLauncherDownload() {
        return getOrDefault("launcher", null);
    }

    public JIPipeInstanceDownload getLauncherUpdaterDownload() {
        return getOrDefault("launcher-updater", null);
    }
}
