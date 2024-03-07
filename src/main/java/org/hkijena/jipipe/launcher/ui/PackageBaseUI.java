package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.ui.JIPipeWorkbench;
import org.hkijena.jipipe.ui.JIPipeWorkbenchPanel;

public abstract class PackageBaseUI extends JIPipeWorkbenchPanel {
    public PackageBaseUI(JIPipeWorkbench workbench) {
        super(workbench);
        initialize();
    }

    private void initialize() {

    }
}
