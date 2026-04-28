package com.project.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


public class DesignConstants {

    
    
    

    
    public static final Color PRIMARY_DARK = new Color(25, 42, 86);      
    public static final Color PRIMARY = new Color(41, 98, 255);          
    public static final Color PRIMARY_LIGHT = new Color(100, 149, 237);  

    
    public static final Color ACCENT = new Color(0, 184, 148);           
    public static final Color SUCCESS = new Color(46, 213, 115);         
    public static final Color WARNING = new Color(255, 159, 67);         
    public static final Color DANGER = new Color(255, 71, 87);           

    
    public static final Color BACKGROUND = new Color(248, 249, 250);     
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_DARK = new Color(243, 244, 246);

    
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    public static final Color TEXT_MUTED = new Color(173, 181, 189);
    public static final Color TEXT_WHITE = Color.WHITE;

    
    public static final Color BORDER_LIGHT = new Color(222, 226, 230);
    public static final Color BORDER_MEDIUM = new Color(206, 212, 218);

    
    
    

    public static final Font FONT_DISPLAY = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_H3 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    
    
    

    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 16;
    public static final int SPACING_LG = 24;
    public static final int SPACING_XL = 32;
    public static final int SPACING_XXL = 48;

    
    
    

    public static final int BUTTON_HEIGHT = 42;
    public static final int INPUT_HEIGHT = 40;
    public static final int BORDER_RADIUS = 8;

    
    
    

    public static Border createShadowBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(SPACING_MD, SPACING_MD, SPACING_MD, SPACING_MD)
        );
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_LIGHT),
                BorderFactory.createEmptyBorder(SPACING_LG, SPACING_LG, SPACING_LG, SPACING_LG)
        );
    }

    public static Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_MEDIUM, 1),
                BorderFactory.createEmptyBorder(SPACING_SM, SPACING_MD, SPACING_SM, SPACING_MD)
        );
    }
}