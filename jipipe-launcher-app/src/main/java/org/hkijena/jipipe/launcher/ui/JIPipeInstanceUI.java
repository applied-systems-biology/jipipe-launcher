package org.hkijena.jipipe.launcher.ui;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.hkijena.jipipe.api.JIPipeProgressInfo;
import org.hkijena.jipipe.extensions.settings.FileChooserSettings;
import org.hkijena.jipipe.launcher.api.JIPipeInstance;
import org.hkijena.jipipe.launcher.api.JIPipeInstanceDownload;
import org.hkijena.jipipe.launcher.api.JIPipeInstanceDownloadType;
import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.launcher.api.runs.DeleteDirectoryRun;
import org.hkijena.jipipe.launcher.api.runs.DownloadFullPackageInstanceRun;
import org.hkijena.jipipe.launcher.api.runs.DuplicateInstanceRun;
import org.hkijena.jipipe.launcher.ui.utils.LauncherUIUtils;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.JIPipeWorkbenchAccess;
import org.hkijena.jipipe.ui.components.AdvancedFileChooser;
import org.hkijena.jipipe.ui.components.ImageFrame;
import org.hkijena.jipipe.ui.running.JIPipeRunExecuterUI;
import org.hkijena.jipipe.utils.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

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
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 32, 8));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        add(buttonPanel, BorderLayout.SOUTH);

        initializeButtonPanel(buttonPanel);
    }

    private void initializeButtonPanel(JPanel buttonPanel) {
        buttonPanel.add(Box.createHorizontalGlue());
        if (instance == null) {
            initializeAddExistingButtonPanel(buttonPanel);

        } else if (instance.isInstalled()) {
            initializeInstalledButtonPanel(buttonPanel);
        } else {
            initializeDownloadButtonPanel(buttonPanel);
        }
        buttonPanel.add(Box.createHorizontalGlue());
    }

    private void initializeDownloadButtonPanel(JPanel buttonPanel) {
        JButton downloadButton = LauncherUIUtils.createHeroButton("Download",
                UIUtils.getIconInverted32FromResources("actions/edit-download.png"),
                null
        );
        JPopupMenu popupMenu = UIUtils.addPopupMenuToButton(downloadButton);
        List<JIPipeInstanceDownload> compatibleDownloads = instance.getCompatibleDownloads(JIPipeInstanceDownloadType.FullPackage);
        if (compatibleDownloads.isEmpty()) {
            JMenuItem menuItem = new JMenuItem("No compatible downloads detected");
            menuItem.setEnabled(false);
            popupMenu.add(menuItem);
        } else {
            for (JIPipeInstanceDownload download : compatibleDownloads) {
                popupMenu.add(UIUtils.createMenuItem(download.getName(),
                        download.renderUrl(),
                        UIUtils.getIcon32FromResources("actions/cm_packfiles.png"),
                        () -> install(download)));
            }
        }
        buttonPanel.add(downloadButton);
    }

    private void initializeInstalledButtonPanel(JPanel buttonPanel) {
        JButton moreButton = LauncherUIUtils.createSecondaryButton("",
                UIUtils.getIcon32FromResources("actions/overflow-menu.png"), null);
        JPopupMenu popupMenu = UIUtils.addPopupMenuToButton(moreButton);
        popupMenu.add(UIUtils.createMenuItem("Open installation directory",
                "Opens the directory where the instance is installed",
                UIUtils.getIconFromResources("actions/folder-open.png"),
                this::openInstalledInstanceDirectory));
        if (!instance.isCustomized()) {
            popupMenu.add(UIUtils.createMenuItem("Label instance",
                    "Marks this instance with a custom label",
                    UIUtils.getIconFromResources("actions/tag.png"),
                    () -> labelInstalledInstanceIfNeeded("Please input the new name of this instance:")));
        }
        popupMenu.add(UIUtils.createMenuItem("Switch version",
                "Installs a different JIPipe version into the instance.",
                UIUtils.getIconFromResources("actions/system-software-install.png"),
                this::switchInstalledInstanceVersion));
        popupMenu.add(UIUtils.createMenuItem("Duplicate",
                "Duplicates the instance into a dedicated copy",
                UIUtils.getIconFromResources("actions/edit-duplicate.png"),
                this::duplicateInstalledInstance));
        popupMenu.add(UIUtils.createMenuItem("Uninstall",
                "Deletes the instance",
                UIUtils.getIconFromResources("actions/bqm-remove.png"),
                this::deleteInstalledInstance));

        List<JIPipeInstanceDownload> compatibleDownloads = instance.getCompatibleDownloads(JIPipeInstanceDownloadType.FullPackage);
        if (!compatibleDownloads.isEmpty()) {
            popupMenu.addSeparator();
            JMenu installNewMenu = new JMenu("Download additional instance");
            for (JIPipeInstanceDownload download : compatibleDownloads) {
                installNewMenu.add(UIUtils.createMenuItem(download.getName(),
                        download.renderUrl(),
                        UIUtils.getIconFromResources("actions/cm_packfiles.png"),
                        () -> install(download)));
            }
            popupMenu.add(installNewMenu);
        }

        buttonPanel.add(moreButton);
        buttonPanel.add(Box.createHorizontalStrut(12));
        JButton runImageJButton = LauncherUIUtils.createSecondaryButton("Start ImageJ",
                UIUtils.getIcon32FromResources("apps/imagej2.png"),
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

    private void duplicateInstalledInstance() {

        Path initialDirectory;
        boolean inDefaultDir = JIPipeLauncherCommons.getInstance().installedInstanceLocatedInDefaultDirectory(instance);
        if(inDefaultDir) {
            initialDirectory = JIPipeLauncherCommons.getInstance().findNewInstanceDirectory(instance.getInstallDirectory().getFileName().toString());
            try {
                Files.createDirectories(initialDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            initialDirectory = instance.getInstallDirectory().getParent();
        }

        AdvancedFileChooser fileChooser = new AdvancedFileChooser(initialDirectory.toFile());
        fileChooser.setDialogTitle("Select empty target directory");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            Path selectedDir = fileChooser.getSelectedFile().toPath();
            if(inDefaultDir && !Objects.equals(selectedDir, initialDirectory)) {
                PathUtils.deleteDirectoryRecursively(initialDirectory, new JIPipeProgressInfo());
            }

            if(!PathUtils.ensureEmptyFolder(this, selectedDir)) {
                return;
            }

            String newName = JOptionPane.showInputDialog(this, "Please set a name for the duplicated instance:");

            if(!StringUtils.isNullOrEmpty(newName)) {
                DuplicateInstanceRun run = new DuplicateInstanceRun(instance.getInstallDirectory(), selectedDir, newName);
                JIPipeRunExecuterUI.runInDialog(getWorkbench(), this, run);
            }
        }
        else if(inDefaultDir) {
            PathUtils.deleteDirectoryRecursively(initialDirectory, new JIPipeProgressInfo());
        }
    }

    private void deleteInstalledInstance() {
        JCheckBox removeFilesToggle = new JCheckBox("Delete instance directory");
        removeFilesToggle.setToolTipText("Removes the directory " + instance.getInstallDirectory());
        removeFilesToggle.setSelected(JIPipeLauncherCommons.getInstance().installedInstanceLocatedInDefaultDirectory(instance));
        if (JOptionPane.showConfirmDialog(this,
                new Object[]{
                        new JLabel("Do you really want to remove the current instance?"),
                        removeFilesToggle
                },
                "Uninstall instance",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            JIPipeLauncherCommons.getInstance().removeInstance(instance);
            if (removeFilesToggle.isSelected()) {
                JIPipeRunExecuterUI.runInDialog(getWorkbench(), this, new DeleteDirectoryRun(instance.getInstallDirectory()));
            }
        }
    }

    private void switchInstalledInstanceVersion() {
        if (!labelInstalledInstanceIfNeeded("<html>To switch the JIPipe version, you need to label this instance.<br/>" +
                "Please input the label for this instance:</html>")) {
            return;
        }
    }

    private boolean labelInstalledInstanceIfNeeded(String message) {
        if (!instance.isCustomized()) {
            String newName = JOptionPane.showInputDialog(this, message);
            if (!StringUtils.isNullOrEmpty(newName)) {
                JIPipeLauncherCommons.getInstance().labelInstalledInstance(instance, newName);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void openInstalledInstanceDirectory() {
        try {
            Desktop.getDesktop().open(instance.getInstallDirectory().toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeAddExistingButtonPanel(JPanel buttonPanel) {
        JButton button = LauncherUIUtils.createHeroButton("Add existing ImageJ",
                UIUtils.getIconInverted32FromResources("actions/albumfolder-importdir.png"),
                this::importExistingInstance);
        buttonPanel.add(button);
    }

    private void importExistingInstance(JButton button) {
        Path directory = FileChooserSettings.openDirectory(this, FileChooserSettings.LastDirectoryKey.External, "Select Fiji.app directory");
        if (directory != null) {
            String newName = JOptionPane.showInputDialog(this, "Please set a name for the imported instance:");

            if(!StringUtils.isNullOrEmpty(newName)) {
                String response = JIPipeLauncherCommons.getInstance().importInstance(directory,newName);
                if (response != null) {
                    JOptionPane.showMessageDialog(this, "The directory could not be imported!\n" +
                                    response,
                            "Add existing ImageJ",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }

    private void install(JIPipeInstanceDownload download) {
        if (download.getType() == JIPipeInstanceDownloadType.FullPackage) {
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
        if (BACKGROUND_IMAGE == null) {
            try {
                BACKGROUND_IMAGE = ImageIO.read(ResourceUtils.getPluginResource("welcome-hero.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return BACKGROUND_IMAGE;
    }
}
