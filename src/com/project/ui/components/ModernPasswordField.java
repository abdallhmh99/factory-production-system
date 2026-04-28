package com.project.ui.components;

import com.project.ui.DesignConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;


public class ModernPasswordField extends JPasswordField {

    public ModernPasswordField(int columns) {
        super(columns);
        setupStyle();
    }

    private void setupStyle() {
        setFont(DesignConstants.FONT_BODY);
        setPreferredSize(new Dimension(getPreferredSize().width,
                DesignConstants.INPUT_HEIGHT));
        setBorder(DesignConstants.createInputBorder());
        setBackground(DesignConstants.SURFACE);
        setForeground(DesignConstants.TEXT_PRIMARY);
        setCaretColor(DesignConstants.PRIMARY);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DesignConstants.PRIMARY, 2),
                        BorderFactory.createEmptyBorder(
                                DesignConstants.SPACING_SM - 1,
                                DesignConstants.SPACING_MD - 1,
                                DesignConstants.SPACING_SM - 1,
                                DesignConstants.SPACING_MD - 1)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBorder(DesignConstants.createInputBorder());
            }
        });
    }
}