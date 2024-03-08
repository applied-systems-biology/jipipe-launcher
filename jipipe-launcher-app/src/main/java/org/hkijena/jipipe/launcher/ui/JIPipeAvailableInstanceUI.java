package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.launcher.api.JIPipeInstance;
import org.hkijena.jipipe.ui.JIPipeWorkbench;

public class JIPipeAvailableInstanceUI extends JIPipeInstanceUI {
    private final JIPipeInstance instance;
    public JIPipeAvailableInstanceUI(JIPipeWorkbench workbench, JIPipeInstance instance) {
        super(workbench);
        this.instance = instance;
    }

    public JIPipeInstance getInstance() {
        return instance;
    }
}
