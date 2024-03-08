package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.JIPipeWorkbenchAccess;
import org.hkijena.jipipe.ui.JIPipeWorkbenchPanel;
import org.hkijena.jipipe.ui.components.ImageFrame;
import org.hkijena.jipipe.utils.ResourceUtils;
import org.hkijena.jipipe.utils.SizeFitMode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class JIPipeInstanceUI extends ImageFrame implements JIPipeWorkbenchAccess {

    private static BufferedImage BACKGROUND_IMAGE;
    private final JIPipeWorkbench workbench;

    public JIPipeInstanceUI(JIPipeWorkbench workbench) {
        super(getCachedBackgroundImage(), false, SizeFitMode.Cover, true);
        this.workbench = workbench;
        initialize();
    }

    private static BufferedImage getCachedBackgroundImage() {
        if(BACKGROUND_IMAGE == null) {
            try {
                BACKGROUND_IMAGE = ImageIO.read(ResourceUtils.getPluginResource("welcome-hero.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return BACKGROUND_IMAGE;
    }

    private void initialize() {

    }

    @Override
    public JIPipeWorkbench getWorkbench() {
        return workbench;
    }
}
