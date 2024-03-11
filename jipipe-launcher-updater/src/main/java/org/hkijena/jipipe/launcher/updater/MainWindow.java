package org.hkijena.jipipe.launcher.updater;

import org.hkijena.jipipe.JIPipe;
import org.hkijena.jipipe.api.notifications.JIPipeNotificationInbox;
import org.hkijena.jipipe.extensions.parameters.StandardParametersPlugin;
import org.hkijena.jipipe.extensions.settings.StandardSettingsPlugin;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.components.tabs.DocumentTabPane;
import org.hkijena.jipipe.ui.running.JIPipeRunnerQueue;
import org.hkijena.jipipe.ui.theme.JIPipeUITheme;
import org.hkijena.jipipe.utils.UIUtils;
import org.scijava.Context;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;

public class MainWindow extends JFrame implements JIPipeWorkbench, WindowListener {

    public MainWindow(boolean launchMode) {
        setTitle("JIPipe Launcher Updater");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        setContentPane(new UpdaterPanel(this));
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

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if(!JIPipeRunnerQueue.getInstance().isEmpty()) {
            if(JOptionPane.showConfirmDialog(this, "The software is currently still working on a few processes.\n" +
                    "Are you sure that you want to exit the application?", "Exit JIPipe Bootstrap", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        Runtime.getRuntime().halt(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    public static void main(String[] args) {

        // UI setup
        JIPipeUITheme.ModernLight.install();

        // Init JIPipe
        JIPipe.createLibNoImageJInstance(Arrays.asList(StandardParametersPlugin.class, StandardSettingsPlugin.class));

        // Init commons
        JIPipeLauncherCommons.getInstance().initialize();

        // Start main window
        MainWindow window = new MainWindow(true);
        window.setIconImage(UIUtils.getJIPipeIcon128());
        window.pack();
        window.setSize(640,480);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
