package com.project.ui;

import com.project.MyExceptions;
import com.project.User.User;
import com.project.User.UserManager;
import com.project.User.UserRole;
import com.project.controller.FileManager;
import com.project.ui.components.*;
import javax.swing.*;
import java.awt.*;


public class LoginScreen extends JFrame {

    private ModernTextField usernameField;
    private ModernPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private ModernButton loginButton;
    private JLabel messageLabel;

    private UserManager userManager;

    public LoginScreen() {
        try {
            userManager = UserManager.getInstance();
        } catch (MyExceptions e) {
            showError("Failed to load user data: " + e.getMessage());
        }

        initializeUI();
    }

    private void initializeUI() {
        setTitle("ProManage - Production Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        
        JPanel mainContainer = new JPanel(new GridLayout(1, 2, 0, 0));

        
        JPanel leftPanel = createBrandingPanel();

        
        JPanel rightPanel = createLoginPanel();

        mainContainer.add(leftPanel);
        mainContainer.add(rightPanel);

        setContentPane(mainContainer);
    }

    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DesignConstants.PRIMARY_DARK);
        panel.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        
        JLabel iconLabel = new JLabel("PM");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
        iconLabel.setForeground(DesignConstants.TEXT_WHITE);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        JLabel titleLabel = new JLabel("ProManage");
        titleLabel.setFont(DesignConstants.FONT_DISPLAY);
        titleLabel.setForeground(DesignConstants.TEXT_WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        JLabel subtitleLabel = new JLabel("Production Management System");
        subtitleLabel.setFont(DesignConstants.FONT_H3);
        subtitleLabel.setForeground(new Color(200, 200, 220));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        JLabel descLabel = new JLabel("<html><center>Streamline your production workflow<br>" +
                "Manage inventory, tasks, and production lines<br>" +
                "All in one powerful platform</center></html>");
        descLabel.setFont(DesignConstants.FONT_BODY);
        descLabel.setForeground(new Color(180, 180, 200));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(iconLabel);
        content.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        content.add(titleLabel);
        content.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        content.add(subtitleLabel);
        content.add(Box.createVerticalStrut(DesignConstants.SPACING_XL));
        content.add(descLabel);

        panel.add(content);

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DesignConstants.BACKGROUND);
        panel.setLayout(new GridBagLayout());

        
        JPanel formContainer = new JPanel();
        formContainer.setBackground(DesignConstants.SURFACE);
        formContainer.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_XXL,
                DesignConstants.SPACING_XXL,
                DesignConstants.SPACING_XXL,
                DesignConstants.SPACING_XXL));
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setPreferredSize(new Dimension(380, 500));

        
        JLabel headerLabel = new JLabel("Welcome Back");
        headerLabel.setFont(DesignConstants.FONT_H1);
        headerLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subheaderLabel = new JLabel("Please login to continue");
        subheaderLabel.setFont(DesignConstants.FONT_BODY);
        subheaderLabel.setForeground(DesignConstants.TEXT_SECONDARY);
        subheaderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContainer.add(headerLabel);
        formContainer.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        formContainer.add(subheaderLabel);
        formContainer.add(Box.createVerticalStrut(DesignConstants.SPACING_XL));

        
        formContainer.add(createFieldGroup("Username", usernameField = new ModernTextField(25, "Enter your username")));
        formContainer.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        
        formContainer.add(createFieldGroup("Password", passwordField = new ModernPasswordField(25)));
        formContainer.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        
        formContainer.add(createRoleGroup());
        formContainer.add(Box.createVerticalStrut(DesignConstants.SPACING_XL));

        
        loginButton = new ModernButton("Login");
        loginButton.setPreferredSize(new Dimension(340, DesignConstants.BUTTON_HEIGHT));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.BUTTON_HEIGHT));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());

        formContainer.add(loginButton);
        formContainer.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));

        
        messageLabel = new JLabel(" ");
        messageLabel.setFont(DesignConstants.FONT_SMALL);
        messageLabel.setForeground(DesignConstants.DANGER);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContainer.add(messageLabel);

        
        passwordField.addActionListener(e -> handleLogin());

        panel.add(formContainer);

        return panel;
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel group = new JPanel();
        group.setBackground(DesignConstants.SURFACE);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(DesignConstants.FONT_BODY_BOLD);
        label.setForeground(DesignConstants.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(label);
        group.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        group.add(field);

        return group;
    }

    private JPanel createRoleGroup() {
        JPanel group = new JPanel();
        group.setBackground(DesignConstants.SURFACE);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("Role");
        label.setFont(DesignConstants.FONT_BODY_BOLD);
        label.setForeground(DesignConstants.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] roles = {"Manager", "Production Supervisor"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(DesignConstants.FONT_BODY);
        roleComboBox.setBackground(DesignConstants.SURFACE);
        roleComboBox.setForeground(DesignConstants.TEXT_PRIMARY);
        roleComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        roleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(label);
        group.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        group.add(roleComboBox);

        return group;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        int roleIndex = roleComboBox.getSelectedIndex();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Please enter username and password", DesignConstants.DANGER);
            return;
        }

        UserRole role = (roleIndex == 0) ? UserRole.MANAGER : UserRole.SUPERVISOR;
        User user = new User(username, password, role);

        if (userManager.checkUser(user)) {
            showMessage("Login successful!", DesignConstants.SUCCESS);

            
            
            

            SwingUtilities.invokeLater(() -> {
                this.dispose();
                if (role == UserRole.MANAGER) {
                    new ManagerDashboard().setVisible(true);
                } else {
                    new SupervisorDashboard().setVisible(true);
                }
            });
        } else {
            showMessage("Invalid username or password", DesignConstants.DANGER);
            passwordField.setText("");
        }
    }

    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }










}