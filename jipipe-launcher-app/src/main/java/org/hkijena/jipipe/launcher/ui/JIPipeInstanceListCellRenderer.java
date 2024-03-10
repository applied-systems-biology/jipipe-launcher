package org.hkijena.jipipe.launcher.ui;

import org.hkijena.jipipe.launcher.api.JIPipeInstance;
import org.hkijena.jipipe.ui.theme.ModernMetalTheme;
import org.hkijena.jipipe.utils.StringUtils;
import org.hkijena.jipipe.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class JIPipeInstanceListCellRenderer extends JPanel implements ListCellRenderer<JIPipeInstance> {

    private final JLabel iconLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel versionLabel = new JLabel();


    public JIPipeInstanceListCellRenderer() {
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        Insets insets = new Insets(4, 4, 4, 4);
        add(iconLabel, new GridBagConstraints(0,
                0,
                1,
                2,
                0,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                insets,
                0,
                0));
        add(nameLabel, new GridBagConstraints(1,
                0,
                1,
                1,
                1,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(2,4,2,4),
                0,
                0
        ));
        add(versionLabel, new GridBagConstraints(1,
                1,
                1,
                1,
                1,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(2,4,2,4),
                0,
                0
        ));

        nameLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
        versionLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends JIPipeInstance> list, JIPipeInstance value, int index, boolean isSelected, boolean cellHasFocus) {

        if(value == null) {
            iconLabel.setIcon(UIUtils.getIconInverted32FromResources("actions/add.png"));
            nameLabel.setText("Add");
            versionLabel.setText("Import an existing ImageJ directory");
            nameLabel.setEnabled(false);
            versionLabel.setEnabled(false);
        }
        else {
            if (value.isInstalled()) {
                iconLabel.setIcon(JIPipeLauncherAppUtils.RESOURCES.getIcon32FromResources("jipipe.png"));
            } else {
                iconLabel.setIcon(JIPipeLauncherAppUtils.RESOURCES.getIcon32FromResources("jipipe-muted.png"));
            }

            nameLabel.setEnabled(value.isInstalled());
            versionLabel.setEnabled(value.isInstalled());

            if(value.isCustomized()) {
                nameLabel.setText("JIPipe [" + StringUtils.orElse(value.getName(), "Custom") + "]");
                versionLabel.setText(value.getVersion() + "-custom");
            }
            else {
                nameLabel.setText(StringUtils.orElse(value.getName(), "JIPipe"));
                versionLabel.setText(value.getVersion());
            }
        }


        if (isSelected) {
            setBackground(ModernMetalTheme.CONTROL_HIGHLIGHTED);
        } else {
            setBackground(UIManager.getColor("List.background"));
        }
        return this;
    }
}
