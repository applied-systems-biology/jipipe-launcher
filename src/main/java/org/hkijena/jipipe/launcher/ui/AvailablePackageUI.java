package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.launcher.api.JIPipeAvailablePackage;
import org.hkijena.jipipe.ui.JIPipeWorkbench;

public class AvailablePackageUI extends PackageBaseUI {
    private final JIPipeAvailablePackage availablePackage;
    public AvailablePackageUI(JIPipeWorkbench workbench, JIPipeAvailablePackage availablePackage) {
        super(workbench);
        this.availablePackage = availablePackage;
    }

    public JIPipeAvailablePackage getAvailablePackage() {
        return availablePackage;
    }
}
