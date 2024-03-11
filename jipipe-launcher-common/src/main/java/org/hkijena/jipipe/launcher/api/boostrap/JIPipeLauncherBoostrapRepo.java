package org.hkijena.jipipe.launcher.api.boostrap;

import org.apache.commons.lang3.SystemUtils;
import org.hkijena.jipipe.launcher.api.repo.JIPipeInstanceDownload;

import java.util.HashMap;

public class JIPipeLauncherBoostrapRepo extends HashMap<String, JIPipeInstanceDownload> {
    public JIPipeInstanceDownload getLauncherDownload() {
        if(SystemUtils.IS_OS_WINDOWS) {
            return getOrDefault("launcher-windows", null);
        }
        else if(SystemUtils.IS_OS_LINUX) {
            return getOrDefault("launcher-linux", null);
        }
        else if(SystemUtils.IS_OS_MAC_OSX) {
            return getOrDefault("launcher-macos",null);
        }
        else {
            throw new UnsupportedOperationException("Unknown operating system!");
        }
    }

    public JIPipeInstanceDownload getInstallerDownload() {
        if(SystemUtils.IS_OS_WINDOWS) {
            return getOrDefault("installer-windows", null);
        }
        else if(SystemUtils.IS_OS_LINUX) {
            return getOrDefault("installer-linux", null);
        }
        else if(SystemUtils.IS_OS_MAC_OSX) {
            return getOrDefault("installer-macos",null);
        }
        else {
            throw new UnsupportedOperationException("Unknown operating system!");
        }
    }
}
