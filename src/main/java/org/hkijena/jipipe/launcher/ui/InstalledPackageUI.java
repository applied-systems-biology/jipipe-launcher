package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.launcher.api.JIPipeInstalledPackage;
import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.JIPipeWorkbenchPanel;

public class InstalledPackageUI extends PackageBaseUI {
    private final JIPipeInstalledPackage installedPackage;
    public InstalledPackageUI(JIPipeWorkbench workbench, JIPipeInstalledPackage installedPackage) {
        super(workbench);
        this.installedPackage = installedPackage;
    }

    public JIPipeInstalledPackage getInstalledPackage() {
        return installedPackage;
    }
}
