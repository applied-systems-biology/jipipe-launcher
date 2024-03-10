package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.launcher.api.JIPipeInstance;
import org.hkijena.jipipe.launcher.api.JIPipeInstanceDownloadType;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherSettings;
import org.hkijena.jipipe.launcher.api.events.InstancesUpdatedEvent;
import org.hkijena.jipipe.launcher.api.events.InstancesUpdatedEventListener;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.JIPipeWorkbenchPanel;
import org.hkijena.jipipe.ui.components.markdown.MarkdownDocument;
import org.hkijena.jipipe.ui.parameters.ParameterPanel;
import org.hkijena.jipipe.ui.running.JIPipeRunnerQueueButton;
import org.hkijena.jipipe.utils.AutoResizeSplitPane;
import org.hkijena.jipipe.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LauncherPanel extends JIPipeWorkbenchPanel implements InstancesUpdatedEventListener {
    private final AutoResizeSplitPane splitPane = new AutoResizeSplitPane(AutoResizeSplitPane.LEFT_RIGHT,
            new AutoResizeSplitPane.DynamicSidebarRatio(300, true));
    private final JList<JIPipeInstance> entryJList = new JList<>();
    private final JIPipeLauncherCommons commons = JIPipeLauncherCommons.getInstance();

    public LauncherPanel(JIPipeWorkbench workbench) {
        super(workbench);
        initialize();

        commons.getInstancesUpdatedEventEmitter().subscribe(this);
        reloadList();
        if(!commons.getSettings().isOfflineMode()) {
            commons.queryAvailableInstances();
        }
    }

    private void reloadList() {
        JIPipeInstance currentSelection = entryJList.getSelectedValue();

        DefaultListModel<JIPipeInstance> model = new DefaultListModel<>();
        List<JIPipeInstance> sortedInstanceList = commons.getSortedInstanceList();
        for (JIPipeInstance instance : sortedInstanceList) {

            // Filter out versions without a full package
            // Users will need to start from one version with existing full packages
            if(instance.isNotInstalled() && instance.getCompatibleDownloads(JIPipeInstanceDownloadType.FullPackage).isEmpty()) {
                continue;
            }

            model.addElement(instance);
        }
        model.addElement(null);

        entryJList.setModel(model);

        if (currentSelection != null) {
            if (sortedInstanceList.contains(currentSelection)) {
//                showPackage(currentSelection);
                entryJList.setSelectedValue(currentSelection, true);
            } else {
                showAnyPackage();
            }
        } else {
            showAnyPackage();
        }
    }

    private void showAnyPackage() {
        JIPipeInstance latestInstalledInstance = commons.findLatestInstalledInstance();
        if(latestInstalledInstance != null) {
            entryJList.setSelectedValue(latestInstalledInstance, true);
        }
        else {
            JIPipeInstance latestAvailableInstance = commons.findLatestAvailableInstance();
            if(latestAvailableInstance != null) {
                entryJList.setSelectedValue(latestAvailableInstance, true);
            }
            else {
                showPackage(null);
            }
        }
    }

    private void showPackage(JIPipeInstance instance) {
        splitPane.setRightComponent(new JIPipeInstanceUI(getWorkbench(), instance));
        revalidate();
        repaint();
        splitPane.applyRatio();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(new JScrollPane(entryJList));
        entryJList.setCellRenderer(new JIPipeInstanceListCellRenderer());
        entryJList.addListSelectionListener(e -> showPackage(entryJList.getSelectedValue()));

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.SOUTH);

        JButton settingsButton = new JButton(UIUtils.getIconFromResources("actions/configure.png"));
        UIUtils.makeFlat25x25(settingsButton);
        settingsButton.addActionListener(e -> openSettings());
        toolBar.add(settingsButton);

        toolBar.add(Box.createHorizontalGlue());

        JIPipeRunnerQueueButton runnerQueueButton = new JIPipeRunnerQueueButton(getWorkbench());
        runnerQueueButton.makeFlat();
        toolBar.add(runnerQueueButton);
    }

    private void openSettings() {
        JIPipeLauncherSettings copy = new JIPipeLauncherSettings(JIPipeLauncherCommons.getInstance().getSettings());
        if(ParameterPanel.showDialog(getWorkbench(),
                copy,
                new MarkdownDocument(""),
                "JIPipe Launcher - Settings",
                ParameterPanel.DEFAULT_DIALOG_FLAGS)) {
            JIPipeLauncherCommons.getInstance().getSettings().setTo(copy);
            JIPipeLauncherCommons.getInstance().writeSettings();
        }
    }

    private void addExistingInstance() {

    }

    @Override
    public void onInstanceRepositoryUpdated(InstancesUpdatedEvent event) {
        reloadList();
    }
}
