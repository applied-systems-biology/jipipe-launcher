package org.hkijena.jipipe.launcher.updater;

import org.hkijena.jipipe.api.JIPipeRunnable;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.launcher.api.runs.UpdateLauncherRun;
import org.hkijena.jipipe.launcher.ui.RunQueueProgressUI;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.JIPipeWorkbenchAccess;
import org.hkijena.jipipe.ui.components.ImageFrame;
import org.hkijena.jipipe.ui.running.JIPipeRunnerQueue;
import org.hkijena.jipipe.ui.theme.ModernMetalTheme;
import org.hkijena.jipipe.utils.ResourceUtils;
import org.hkijena.jipipe.utils.SizeFitMode;
import org.hkijena.jipipe.utils.UIUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;

public class UpdaterPanel extends ImageFrame implements JIPipeWorkbenchAccess, JIPipeRunnable.FinishedEventListener, JIPipeRunnable.InterruptedEventListener {
    private final JIPipeWorkbench workbench;

    public UpdaterPanel(JIPipeWorkbench workbench) {
        super(loadBackgroundImage(), false, SizeFitMode.Cover, true);
        this.workbench = workbench;

        initialize();

        JIPipeRunnerQueue.getInstance().getFinishedEventEmitter().subscribe(this);
        JIPipeRunnerQueue.getInstance().getInterruptedEventEmitter().subscribe(this);
        SwingUtilities.invokeLater(this::runWorkload);
    }

    private void runWorkload() {
        JIPipeLauncherCommons commons = JIPipeLauncherCommons.getInstance();
        // Launch mode -> update
        if(!Files.exists(commons.getLauncherJarPath()) || !commons.getSettings().isOfflineMode()) {
            commons.bootstrapUpdateLauncher();
        }
        else {
            // Launch the existing installer
            commons.launchLauncher();
        }
    }

    @Override
    public void onRunnableFinished(JIPipeRunnable.FinishedEvent event) {
       if(event.getRun() instanceof UpdateLauncherRun) {
            // Updated the launcher -> run it now
            JIPipeLauncherCommons commons = JIPipeLauncherCommons.getInstance();
            commons.launchLauncher();
            commons.exitLater(1000);
        }
    }

    @Override
    public void onRunnableInterrupted(JIPipeRunnable.InterruptedEvent event) {
        JIPipeLauncherCommons commons = JIPipeLauncherCommons.getInstance();
        if(Files.exists(commons.getLauncherJarPath())) {
            // Updated the launcher -> run it now
            commons.launchLauncher();
            commons.exitLater(1000);
        }
        else {
            JOptionPane.showMessageDialog(this, "Something went wrong while updating the launcher.\n" +
                            "Please ensure a stable internet connection.\n" +
                            "If this is not the case, download the JIPipe offline packages.",
                    "Updating launcher",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Top logo
        ImageFrame logoPanel = new ImageFrame(UIUtils.getLogo(), false, SizeFitMode.Fit, true);
        logoPanel.setMinimumSize(new Dimension(400, 200));
        logoPanel.setPreferredSize(new Dimension(400, 200));
        logoPanel.setScaleFactor(0.7);
        logoPanel.setOpaque(false);
        add(logoPanel, BorderLayout.NORTH);

        // Center
        JLabel pleaseWaitLabel = new JLabel("Preparing launcher ...", UIUtils.getIcon32FromResources("actions/hourglass-half.png"),JLabel.LEFT);
        pleaseWaitLabel.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        pleaseWaitLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        pleaseWaitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(pleaseWaitLabel, BorderLayout.CENTER);

        // Bottom
//        JIPipeRunnerQueueButton runnerQueueButton = new JIPipeRunnerQueueButton(getWorkbench());
//        runnerQueueButton.setHorizontalAlignment(SwingConstants.LEFT);
//        add(runnerQueueButton, BorderLayout.SOUTH);
        RunQueueProgressUI queuePanelUI = new RunQueueProgressUI();
        queuePanelUI.setBorder( BorderFactory.createMatteBorder(1,0,0,0, ModernMetalTheme.CONTROL_TOGGLED));
        add(queuePanelUI, BorderLayout.SOUTH);
    }

    @Override
    public JIPipeWorkbench getWorkbench() {
        return workbench;
    }

    private static BufferedImage loadBackgroundImage() {
        try {
            return ImageIO.read(ResourceUtils.getPluginResource("welcome-hero.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
