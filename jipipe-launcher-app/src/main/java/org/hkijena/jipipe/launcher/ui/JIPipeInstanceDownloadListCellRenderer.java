package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.launcher.api.repo.JIPipeInstanceDownload;
import org.hkijena.jipipe.ui.theme.ModernMetalTheme;
import org.hkijena.jipipe.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class JIPipeInstanceDownloadListCellRenderer extends JLabel implements ListCellRenderer<JIPipeInstanceDownload> {
    public JIPipeInstanceDownloadListCellRenderer() {
        setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        setIcon(UIUtils.getIcon32FromResources("actions/archive.png"));
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends JIPipeInstanceDownload> list, JIPipeInstanceDownload value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getName());
        if (isSelected) {
            setBackground(ModernMetalTheme.CONTROL_HIGHLIGHTED);
        } else {
            setBackground(UIManager.getColor("List.background"));
        }
        return this;
    }
}
