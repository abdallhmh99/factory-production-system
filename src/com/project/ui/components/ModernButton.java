package com.project.ui.components;

import com.project.ui.DesignConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class ModernButton extends JButton {

    private Color defaultColor;
    private Color hoverColor;
    private Color pressedColor;
    private boolean isPrimary;

    public ModernButton(String text) {
        this(text, true);
    }

    public ModernButton(String text, boolean isPrimary) {
        super(text);
        this.isPrimary = isPrimary;

        if (isPrimary) {
            this.defaultColor = DesignConstants.PRIMARY;
            this.hoverColor = DesignConstants.PRIMARY_LIGHT;
            this.pressedColor = DesignConstants.PRIMARY_DARK;
        } else {
            this.defaultColor = DesignConstants.SURFACE_DARK;
            this.hoverColor = DesignConstants.BORDER_MEDIUM;
            this.pressedColor = DesignConstants.BORDER_LIGHT;
        }

        setupStyle();
    }

    private void setupStyle() {
        setFont(DesignConstants.FONT_BUTTON);
        setForeground(isPrimary ? DesignConstants.TEXT_WHITE : DesignConstants.TEXT_PRIMARY);
        setBackground(defaultColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(true);

        setPreferredSize(new Dimension(120, DesignConstants.BUTTON_HEIGHT));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(defaultColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(pressedColor);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(hoverColor);
                }
            }
        });
    }

    public void setDanger() {
        this.defaultColor = DesignConstants.DANGER;
        this.hoverColor = DesignConstants.DANGER.darker();
        this.pressedColor = DesignConstants.DANGER.darker().darker();
        setForeground(DesignConstants.TEXT_WHITE);
        setBackground(defaultColor);
        isPrimary = true;
    }

    public void setSuccess() {
        this.defaultColor = DesignConstants.SUCCESS;
        this.hoverColor = DesignConstants.SUCCESS.darker();
        this.pressedColor = DesignConstants.SUCCESS.darker().darker();
        setForeground(DesignConstants.TEXT_WHITE);
        setBackground(defaultColor);
        isPrimary = true;
    }
}