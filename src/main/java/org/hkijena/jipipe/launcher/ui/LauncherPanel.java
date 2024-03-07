package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.launcher.api.JIPipeAvailablePackage;
import org.hkijena.jipipe.launcher.api.JIPipeInstalledPackage;
import org.hkijena.jipipe.launcher.api.JIPipePackage;
import org.hkijena.jipipe.launcher.api.JIPipePackageRepository;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.JIPipeWorkbenchPanel;
import org.hkijena.jipipe.utils.AutoResizeSplitPane;
import org.hkijena.jipipe.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class LauncherPanel extends JIPipeWorkbenchPanel {
    private final AutoResizeSplitPane splitPane = new AutoResizeSplitPane(AutoResizeSplitPane.LEFT_RIGHT,
            new AutoResizeSplitPane.DynamicSidebarRatio(300, true));
    private final JList<JIPipePackage> entryJList = new JList<>();

    public LauncherPanel(JIPipeWorkbench workbench) {
        super(workbench);
        initialize();
        reloadList();

        showAnyPackage();
    }

    private void reloadList() {
        JIPipePackage currentSelection = entryJList.getSelectedValue();

        DefaultListModel<JIPipePackage> model = new DefaultListModel<>();
        for (JIPipeInstalledPackage installedPackage : JIPipePackageRepository.getInstance().getInstalledPackageList()) {
            model.addElement(installedPackage);
        }
        for (JIPipeAvailablePackage availablePackage : JIPipePackageRepository.getInstance().getAvailablePackageList()) {
            model.addElement(availablePackage);
        }
        entryJList.setModel(model);

        if (currentSelection != null) {
            if (JIPipePackageRepository.getInstance().contains(currentSelection)) {
                showPackage(currentSelection);
            } else {
                showAnyPackage();
            }
        } else {
            showAnyPackage();
        }
    }

    private void showAnyPackage() {
        if(!JIPipePackageRepository.getInstance().getInstalledPackageList().isEmpty()) {
            showPackage(JIPipePackageRepository.getInstance().findLatestInstalledPackage());
        }
        else if(!JIPipePackageRepository.getInstance().getAvailablePackageList().isEmpty()) {
            showPackage(JIPipePackageRepository.getInstance().findLatestAvailablePackage());
        }
        else {
            showNoPackage();
        }
    }

    private void showNoPackage() {

    }

    private void showPackage(JIPipePackage aPackage) {
        revalidate();
        repaint();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(new JScrollPane(entryJList));
    }
}
