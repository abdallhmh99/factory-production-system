package com.project.ui.components;

import com.project.ui.DesignConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;


public class ModernTextField extends JTextField {

    private String placeholder;

    public ModernTextField(int columns) {
        this(columns, "");
    }

    public ModernTextField(int columns, String placeholder) {
        super(columns);
        this.placeholder = placeholder;
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

        
        if (!placeholder.isEmpty()) {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getText().isEmpty() && !placeholder.isEmpty() && !hasFocus()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(DesignConstants.TEXT_MUTED);
            g2.setFont(DesignConstants.FONT_BODY);
            g2.drawString(placeholder,
                    getInsets().left,
                    getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
            g2.dispose();
        }
    }
}