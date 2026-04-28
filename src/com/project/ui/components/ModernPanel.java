package com.project.ui.components;

import com.project.ui.DesignConstants;
import javax.swing.*;
import java.awt.*;


public class ModernPanel extends JPanel {

    public ModernPanel() {
        setBackground(DesignConstants.SURFACE);
        setBorder(DesignConstants.createShadowBorder());
    }

    public ModernPanel(LayoutManager layout) {
        setLayout(layout);
        setBackground(DesignConstants.SURFACE);
        setBorder(DesignConstants.createShadowBorder());
    }
}