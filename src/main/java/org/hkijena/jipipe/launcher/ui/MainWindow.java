package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.api.notifications.JIPipeNotificationInbox;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.components.tabs.DocumentTabPane;
import org.hkijena.jipipe.ui.theme.JIPipeUITheme;
import org.hkijena.jipipe.utils.UIUtils;
import org.scijava.Context;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame implements JIPipeWorkbench {

//    public static final JIPipeResourceManager RESOURCES = new JIPipeResourceManager(MainWindow.class, "org/hkijena/jipipe/launcher");

    public MainWindow() {
        initialize();
    }

    private void initialize() {
        setTitle("JIPipe Launcher");
        setContentPane(new LauncherPanel(this));
    }

    public static void main(String[] args) {
        // UI setup
        JIPipeUITheme.ModernLight.install();

        // Start main window
        MainWindow window = new MainWindow();
        window.setIconImage(UIUtils.getJIPipeIcon128());
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.pack();
        window.setSize(1024,768);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    @Override
    public Window getWindow() {
        return this;
    }

    @Override
    public void sendStatusBarText(String text) {

    }

    @Override
    public boolean isProjectModified() {
        return false;
    }

    @Override
    public void setProjectModified(boolean modified) {

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public DocumentTabPane getDocumentTabPane() {
        return null;
    }

    @Override
    public JIPipeNotificationInbox getNotificationInbox() {
        return null;
    }
}