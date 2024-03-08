package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.launcher.api.JIPipeInstance;
import org.hkijena.jipipe.launcher.api.JIPipeInstanceRepository;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherSettings;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.JIPipeWorkbenchPanel;
import org.hkijena.jipipe.ui.components.markdown.MarkdownDocument;
import org.hkijena.jipipe.ui.parameters.ParameterPanel;
import org.hkijena.jipipe.ui.running.JIPipeRunnerQueueButton;
import org.hkijena.jipipe.utils.AutoResizeSplitPane;
import org.hkijena.jipipe.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class LauncherPanel extends JIPipeWorkbenchPanel implements JIPipeInstanceRepository.UpdatedEventListener {
    private final AutoResizeSplitPane splitPane = new AutoResizeSplitPane(AutoResizeSplitPane.LEFT_RIGHT,
            new AutoResizeSplitPane.DynamicSidebarRatio(300, true));
    private final JList<JIPipeInstance> entryJList = new JList<>();

    public LauncherPanel(JIPipeWorkbench workbench) {
        super(workbench);
        initialize();

        JIPipeLauncherCommons.getInstance().getRepository().getUpdatedEventEmitter().subscribe(this);
        JIPipeLauncherCommons.getInstance().updateRepository();
    }

    private void reloadList() {
        JIPipeInstanceRepository repository = JIPipeLauncherCommons.getInstance().getRepository();
        JIPipeInstance currentSelection = entryJList.getSelectedValue();

        DefaultListModel<JIPipeInstance> model = new DefaultListModel<>();
        for (JIPipeInstance instance : repository.getSortedInstanceList()) {
            model.addElement(instance);
        }

        entryJList.setModel(model);

        if (currentSelection != null) {
            if (repository.contains(currentSelection)) {
                showPackage(currentSelection);
            } else {
                showAnyPackage();
            }
        } else {
            showAnyPackage();
        }
    }

    private void showAnyPackage() {
        JIPipeInstanceRepository repository = JIPipeLauncherCommons.getInstance().getRepository();
        JIPipeInstance latestInstalledInstance = repository.findLatestInstalledInstance();
        if(latestInstalledInstance != null) {
            entryJList.setSelectedValue(latestInstalledInstance, true);
        }
        else {
            JIPipeInstance latestAvailableInstance = repository.findLatestAvailableInstance();
            if(latestAvailableInstance != null) {
                entryJList.setSelectedValue(latestAvailableInstance, true);
            }
            else {
                showPackage(null);
            }
        }
    }

    private void showPackage(JIPipeInstance instance) {
        if (instance == null) {
            splitPane.setRightComponent(new JIPipeNoInstanceUI(getWorkbench()));
        }
        else if(instance.isNotInstalled()) {
            splitPane.setRightComponent(new JIPipeAvailableInstanceUI(getWorkbench(), instance));
        }
        else if(instance.isInstalled()) {
            splitPane.setRightComponent(new JIPipeInstalledInstanceUI(getWorkbench(), instance));
        }
        else {
            splitPane.setRightComponent(new JIPipeNoInstanceUI(getWorkbench()));
        }
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
    public void onInstanceRepositoryUpdated(JIPipeInstanceRepository.UpdatedEvent event) {
        reloadList();
    }
}
