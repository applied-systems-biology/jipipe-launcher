package org.hkijena.jipipe.launcher.ui;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.hkijena.jipipe.launcher.api.JIPipeInstance;
import org.hkijena.jipipe.launcher.api.JIPipeInstanceDownload;
import org.hkijena.jipipe.launcher.api.JIPipeInstanceDownloadType;
import org.hkijena.jipipe.launcher.api.runs.DownloadFullPackageInstanceRun;
import org.hkijena.jipipe.launcher.ui.utils.LauncherUIUtils;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.JIPipeWorkbenchAccess;
import org.hkijena.jipipe.ui.components.ImageFrame;
import org.hkijena.jipipe.ui.running.JIPipeRunExecuterUI;
import org.hkijena.jipipe.utils.ResourceUtils;
import org.hkijena.jipipe.utils.SizeFitMode;
import org.hkijena.jipipe.utils.UIUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class JIPipeInstanceUI extends ImageFrame implements JIPipeWorkbenchAccess {

    private static BufferedImage BACKGROUND_IMAGE;
    private final JIPipeWorkbench workbench;
    private final JIPipeInstance instance;


    public JIPipeInstanceUI(JIPipeWorkbench workbench, JIPipeInstance instance) {
        super(getCachedBackgroundImage(), false, SizeFitMode.Cover, true);
        this.workbench = workbench;
        this.instance = instance;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Top info
        ImageFrame logoPanel = new ImageFrame(UIUtils.getLogo(), false, SizeFitMode.Fit, true);
        logoPanel.setMinimumSize(new Dimension(400, 250));
        logoPanel.setPreferredSize(new Dimension(400, 250));
        logoPanel.setScaleFactor(0.7);
        logoPanel.setOpaque(false);
        add(logoPanel, BorderLayout.NORTH);

        // Bottom button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8,8,32,8));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        add(buttonPanel, BorderLayout.SOUTH);

        initializeButtonPanel(buttonPanel);
    }

    private void initializeButtonPanel(JPanel buttonPanel) {
        buttonPanel.add(Box.createHorizontalGlue());
        if(instance == null) {
            initializeAddExistingButtonPanel(buttonPanel);

        }
        else if(instance.isInstalled()) {
           initializeInstalledButtonPanel(buttonPanel);
        }
        else {
          initializeDownloadButtonPanel(buttonPanel);
        }
        buttonPanel.add(Box.createHorizontalGlue());
    }

    private void initializeDownloadButtonPanel(JPanel buttonPanel) {
        JButton button = LauncherUIUtils.createHeroButton("Download",
                UIUtils.getIconInverted32FromResources("actions/edit-download.png"),
                (b) -> {}
        );
        JPopupMenu popupMenu = UIUtils.addPopupMenuToButton(button);
        List<JIPipeInstanceDownload> compatibleDownloads = instance.getCompatibleDownloads();
        if(compatibleDownloads.isEmpty()) {
            JMenuItem menuItem = new JMenuItem("No compatible downloads detected");
            menuItem.setEnabled(false);
            popupMenu.add(menuItem);
        }
        else {
            for (JIPipeInstanceDownload download : compatibleDownloads) {
                popupMenu.add(UIUtils.createMenuItem(download.getName(),
                        download.renderUrl(),
                        UIUtils.getIcon32FromResources("actions/cm_packfiles.png"),
                        () -> install(download)));
            }
        }
        buttonPanel.add(button);
    }

    private void initializeInstalledButtonPanel(JPanel buttonPanel) {
        JButton runImageJButton = LauncherUIUtils.createSecondaryButton("Start ImageJ",
                UIUtils.getIconInverted32FromResources("apps/imagej2.png"),
                (button) -> {
                    LauncherUIUtils.buttonTimeout(button, 4000);
                    instance.startImageJ(new DefaultExecuteResultHandler());
                });
        buttonPanel.add(runImageJButton);
        buttonPanel.add(Box.createHorizontalStrut(12));
        JButton runJIPipeButton = LauncherUIUtils.createHeroButton("Start JIPipe",
                UIUtils.getIconInverted32FromResources("actions/play.png"),
                (button) -> {
                    LauncherUIUtils.buttonTimeout(button, 4000);
                    instance.startJIPipe(new DefaultExecuteResultHandler());
                });
        buttonPanel.add(runJIPipeButton);
    }

    private void initializeAddExistingButtonPanel(JPanel buttonPanel) {
        JButton button = LauncherUIUtils.createHeroButton("Add existing ImageJ",
                UIUtils.getIconInverted32FromResources("actions/albumfolder-importdir.png"),
                this::importExisting);
        buttonPanel.add(button);
    }

    private void importExisting(JButton button) {

    }

    private void install(JIPipeInstanceDownload download) {
        if(download.getType() == JIPipeInstanceDownloadType.FullPackage) {
            DownloadFullPackageInstanceRun run = new DownloadFullPackageInstanceRun(instance, download);
            JIPipeRunExecuterUI.runInDialog(getWorkbench(), this, run);
        }
    }

    public JIPipeInstance getInstance() {
        return instance;
    }

    @Override
    public JIPipeWorkbench getWorkbench() {
        return workbench;
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
}
