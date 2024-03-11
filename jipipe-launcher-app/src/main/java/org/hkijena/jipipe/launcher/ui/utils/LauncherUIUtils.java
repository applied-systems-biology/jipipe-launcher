package org.hkijena.jipipe.launcher.ui.utils;

import org.hkijena.jipipe.launcher.api.JIPipeLauncherCommons;
import org.hkijena.jipipe.ui.components.RoundedButtonUI;
import org.hkijena.jipipe.utils.ui.RoundedLineBorder;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LauncherUIUtils {
    private LauncherUIUtils() {

    }

    public static void buttonTimeout(JButton button, int timeout) {
        button.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        button.setEnabled(false);
        JIPipeLauncherCommons.getInstance().doLater(timeout, () -> {
            button.setEnabled(true);
            button.setCursor(Cursor.getDefaultCursor());
        });
    }

    public static JButton createHeroButton(String text, Icon icon, Consumer<JButton> action) {
        Color colorSuccess = new Color(0x5CB85C);
        Color colorHover = new Color(0x4f9f4f);

        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setBackground(colorSuccess);
        button.setForeground(Color.WHITE);
        button.setUI(new RoundedButtonUI(8, colorHover, colorHover));
        button.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        if(action != null) {
            button.addActionListener(e -> action.accept(button));
        }
        return button;
    }

    public static JButton createSecondaryButton(String text, Icon icon, Consumer<JButton> action) {
        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setOpaque(false);
        button.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
        button.setBorder(BorderFactory.createCompoundBorder(new RoundedLineBorder(new Color(0xabb8c3), 1, 8),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        if(action != null) {
            button.addActionListener(e -> action.accept(button));
        }
        return button;
    }
}
