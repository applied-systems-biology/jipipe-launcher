package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.JIPipe;
import org.hkijena.jipipe.api.notifications.JIPipeNotificationInbox;
import org.hkijena.jipipe.extensions.parameters.StandardParametersPlugin;
import org.hkijena.jipipe.extensions.settings.StandardSettingsPlugin;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.components.SplashScreen;
import org.hkijena.jipipe.ui.components.tabs.DocumentTabPane;
import org.hkijena.jipipe.ui.theme.JIPipeUITheme;
import org.hkijena.jipipe.utils.UIUtils;
import org.scijava.Context;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;

public class MainWindow extends JFrame implements JIPipeWorkbench, WindowListener {


    public MainWindow() {
        initialize();
    }

    private void initialize() {
        setTitle("JIPipe Launcher");
        setContentPane(new LauncherPanel(this));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
    }

    public static void main(String[] args) {
        // UI setup
        JIPipeUITheme.ModernLight.install();

//        SplashScreen.getInstance().showSplash(null);

        // Init JIPipe
        JIPipe.createLibNoImageJInstance(Arrays.asList(StandardParametersPlugin.class, StandardSettingsPlugin.class));

        // Init commons
        JIPipeLauncherCommons.getInstance().initialize();

        // Start main window
        MainWindow window = new MainWindow();
        window.setIconImage(UIUtils.getJIPipeIcon128());
        window.pack();
        window.setSize(1024,768);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

//        SplashScreen.getInstance().hideSplash();
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
}