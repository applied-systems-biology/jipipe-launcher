package org.hkijena.jipipe.launcher.api.runs;

import org.apache.commons.lang3.SystemUtils;
import org.hkijena.jipipe.api.AbstractJIPipeRunnable;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.utils.BufferedImageUtils;
import org.hkijena.jipipe.utils.PathUtils;
import org.hkijena.jipipe.utils.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Deprecated
public class CreateApplicationIconsRun extends AbstractJIPipeRunnable {
    @Override
    public String getTaskLabel() {
        return "Create application launchers";
    }

    @Override
    public void run() {
        getProgressInfo().setLogToStdOut(true);

        if(SystemUtils.IS_OS_WINDOWS) {
            // TODO: implement
        }
        else if(SystemUtils.IS_OS_LINUX) {
            Path desktopFilePath;
            if (System.getProperties().containsKey("XDG_DATA_HOME") && !StringUtils.isNullOrEmpty(System.getProperty("XDG_DATA_HOME"))) {
                desktopFilePath = Paths.get(System.getProperty("XDG_DATA_HOME")).resolve("applications").resolve("jipipe-launcher.desktop");
            } else {
                desktopFilePath = Paths.get(System.getProperty("user.home")).resolve(".local")
                        .resolve("share").resolve("applications").resolve("jipipe-launcher.desktop");
            }
            if(!Files.exists(desktopFilePath)) {

                exportIcon(JIPipeLauncherCommons.getInstance().getResources().getIconFromResources("jipipe.png"), 16);
                exportIcon(JIPipeLauncherCommons.getInstance().getResources().getIcon32FromResources("jipipe.png"), 32);
                exportIcon(JIPipeLauncherCommons.getInstance().getResources().getIcon64FromResources("jipipe.png"), 64);
                exportIcon(JIPipeLauncherCommons.getInstance().getResources().getIcon128FromResources("jipipe.png"), 128);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("[Desktop Entry]\n")
                        .append("Name=JIPipe\n")
                        .append("Exec=").append(JIPipeLauncherCommons.getInstance().getUpdaterJarPath()).append(" --launch-jipipe\n")
                        .append("Icon=jipipe\n")
                        .append("Type=Application\n")
                        .append("Categories=Science;\n")
                        .append("Comment=Visually design image analysis workflows\n")
                        .append("Terminal=false\n")
                        .append("StartupNotify=true\n")
                        .append("NoDisplay=false\n");
                try {
                    Files.write(desktopFilePath, stringBuilder.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void exportIcon(ImageIcon icon, int size) {
        Path iconFilePath;
        if (System.getProperties().containsKey("XDG_DATA_HOME") && !StringUtils.isNullOrEmpty(System.getProperty("XDG_DATA_HOME"))) {
            iconFilePath = Paths.get(System.getProperty("XDG_DATA_HOME")).resolve("icons").resolve("hicolor")
                    .resolve(size + "x" + size).resolve("apps").resolve("jipipe.png");
        } else {
            iconFilePath = Paths.get(System.getProperty("user.home")).resolve(".local")
                    .resolve("share").resolve("icons").resolve("hicolor")
                    .resolve(size + "x" + size).resolve("apps").resolve("jipipe.png");
        }
        if(!Files.exists(iconFilePath)) {
            PathUtils.ensureParentDirectoriesExist(iconFilePath);
            try {
                ImageIO.write(BufferedImageUtils.toBufferedImage(icon.getImage(), BufferedImage.TYPE_INT_ARGB), "PNG", iconFilePath.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
