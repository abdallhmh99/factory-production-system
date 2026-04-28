package com.project.ui;

import com.project.controller.FileManager;
import com.project.controller.InventoryManager;
import com.project.controller.ProductionManager;
import com.project.ui.components.ModernButton;
import com.project.ui.supervisor.*;
import javax.swing.*;
import java.awt.*;


public class SupervisorDashboard extends JFrame {

    private InventoryManager inventoryManager;
    private ProductionManager productionManager;
    private FileManager fileManager;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private ModernButton logoutBtn;

    public SupervisorDashboard() {
        inventoryManager = InventoryManager.getInstance();
        productionManager = new ProductionManager();
        fileManager = new FileManager();

        initializeUI();
        setupAutoSave();
    }

    private void initializeUI() {
        setTitle("ProManage - Supervisor Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);

        
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(DesignConstants.BACKGROUND);

        
        JPanel sidebar = createSidebar();

        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(DesignConstants.BACKGROUND);

        
        contentPanel.add(new SupervisorOverviewPanel(inventoryManager, productionManager, fileManager), "overview");
        contentPanel.add(new InventoryManagementPanel(inventoryManager, fileManager), "inventory");
        contentPanel.add(new ProductManagementPanel(inventoryManager), "products");
        contentPanel.add(new TaskManagementPanel(inventoryManager, productionManager), "tasks");
        contentPanel.add(new ProductionReportsPanel(inventoryManager, productionManager), "reports");

        mainContainer.add(sidebar, BorderLayout.WEST);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainContainer);

        
        cardLayout.show(contentPanel, "overview");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(DesignConstants.PRIMARY_DARK);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setLayout(new BorderLayout());

        
        JPanel header = createSidebarHeader();

        
        JPanel navigation = createNavigation();

        
        JPanel footer = createSidebarFooter();

        sidebar.add(header, BorderLayout.NORTH);
        sidebar.add(navigation, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createSidebarHeader() {
        JPanel header = new JPanel();
        header.setBackground(DesignConstants.PRIMARY_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_XL,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_XL,
                DesignConstants.SPACING_LG
        ));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel logoLabel = new JLabel("PM");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoLabel.setForeground(DesignConstants.TEXT_WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("ProManage");
        titleLabel.setFont(DesignConstants.FONT_H2);
        titleLabel.setForeground(DesignConstants.TEXT_WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel("Production Supervisor");
        roleLabel.setFont(DesignConstants.FONT_SMALL);
        roleLabel.setForeground(new Color(180, 180, 200));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(logoLabel);
        header.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        header.add(roleLabel);

        return header;
    }

    private JPanel createNavigation() {
        JPanel nav = new JPanel();
        nav.setBackground(DesignConstants.PRIMARY_DARK);
        nav.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_MD, 0, DesignConstants.SPACING_MD, 0));
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));

        
        NavButton overviewBtn = new NavButton("Overview", true);
        overviewBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "overview");
            highlightNavButton(overviewBtn, nav);
        });

        
        NavButton inventoryBtn = new NavButton("Inventory Management", false);
        inventoryBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "inventory");
            highlightNavButton(inventoryBtn, nav);
        });

        
        NavButton productsBtn = new NavButton("Product Management", false);
        productsBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "products");
            highlightNavButton(productsBtn, nav);
        });

        
        NavButton tasksBtn = new NavButton("Task Management", false);
        tasksBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "tasks");
            highlightNavButton(tasksBtn, nav);
        });

        
        NavButton reportsBtn = new NavButton("Production Reports", false);
        reportsBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "reports");
            highlightNavButton(reportsBtn, nav);
        });

        nav.add(overviewBtn);
        nav.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        nav.add(inventoryBtn);
        nav.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        nav.add(productsBtn);
        nav.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        nav.add(tasksBtn);
        nav.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        nav.add(reportsBtn);

        return nav;
    }

    
    private class NavButton extends JButton {
        private boolean isActive;

        public NavButton(String text, boolean isActive) {
            super(text);
            this.isActive = isActive;
            setupStyle();
        }

        private void setupStyle() {
            setFont(DesignConstants.FONT_BODY);
            setForeground(DesignConstants.TEXT_WHITE);
            setBackground(isActive ? DesignConstants.PRIMARY : DesignConstants.PRIMARY_DARK);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            setBorder(BorderFactory.createEmptyBorder(
                    DesignConstants.SPACING_MD,
                    DesignConstants.SPACING_LG,
                    DesignConstants.SPACING_MD,
                    DesignConstants.SPACING_LG
            ));

            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!isActive) {
                        setBackground(new Color(35, 60, 110));
                    }
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!isActive) {
                        setBackground(DesignConstants.PRIMARY_DARK);
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            setBackground(active ? DesignConstants.PRIMARY : DesignConstants.PRIMARY_DARK);
        }
    }

    private void highlightNavButton(NavButton activeButton, JPanel navPanel) {
        for (Component comp : navPanel.getComponents()) {
            if (comp instanceof NavButton) {
                NavButton btn = (NavButton) comp;
                btn.setActive(btn == activeButton);
            }
        }
    }

    private JPanel createSidebarFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(DesignConstants.PRIMARY_DARK);
        footer.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_XL,
                DesignConstants.SPACING_LG
        ));
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));

        logoutBtn = new ModernButton("Logout");
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.BUTTON_HEIGHT));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setDanger();
        logoutBtn.addActionListener(e -> handleLogout());

        footer.add(logoutBtn);

        return footer;
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginScreen().setVisible(true);
        }
    }

    
    private void setupAutoSave() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Saving data before exit...");
                fileManager.saveInventory();
                System.out.println("Data saved successfully!");
            }
        });
    }
}