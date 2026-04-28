package com.project.ui.manager;

import com.project.MyExceptions;
import com.project.User.User;
import com.project.User.UserManager;
import com.project.User.UserRole;
import com.project.ui.DesignConstants;
import com.project.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class UserManagementPanel extends JPanel {

    private UserManager userManager;

    private JTable usersTable;
    private DefaultTableModel tableModel;

    private ModernButton addUserBtn;
    private ModernButton deleteUserBtn;
    private ModernButton refreshBtn;

    public UserManagementPanel() {
        try {
            userManager = UserManager.getInstance();
        } catch (MyExceptions e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
        loadUsers();
    }

    private void initializeUI() {
        
        add(createHeader(), BorderLayout.NORTH);

        
        JPanel mainContent = new JPanel(new BorderLayout(DesignConstants.SPACING_LG, DesignConstants.SPACING_LG));
        mainContent.setBackground(DesignConstants.BACKGROUND);
        mainContent.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG
        ));

        
        mainContent.add(createTablePanel(), BorderLayout.CENTER);

        
        mainContent.add(createActionPanel(), BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(DesignConstants.SURFACE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DesignConstants.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(
                        DesignConstants.SPACING_LG,
                        DesignConstants.SPACING_LG,
                        DesignConstants.SPACING_LG,
                        DesignConstants.SPACING_LG
                )
        ));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Manage system users and access");
        subtitleLabel.setFont(DesignConstants.FONT_BODY);
        subtitleLabel.setForeground(DesignConstants.TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        titlePanel.add(subtitleLabel);

        header.add(titlePanel, BorderLayout.WEST);

        return header;
    }

    private JPanel createTablePanel() {
        ModernPanel panel = new ModernPanel(new BorderLayout());

        String[] columns = {"Username", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.setFont(DesignConstants.FONT_BODY);
        usersTable.setRowHeight(40);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.getTableHeader().setFont(DesignConstants.FONT_BODY_BOLD);
        usersTable.getTableHeader().setBackground(DesignConstants.SURFACE_DARK);
        usersTable.setGridColor(DesignConstants.BORDER_LIGHT);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.SPACING_MD, 0));
        panel.setBackground(DesignConstants.BACKGROUND);

        addUserBtn = new ModernButton("Add User");
        addUserBtn.setPreferredSize(new Dimension(120, DesignConstants.BUTTON_HEIGHT));
        addUserBtn.setSuccess();
        addUserBtn.addActionListener(e -> showAddUserDialog());

        deleteUserBtn = new ModernButton("Delete User");
        deleteUserBtn.setPreferredSize(new Dimension(120, DesignConstants.BUTTON_HEIGHT));
        deleteUserBtn.setDanger();
        deleteUserBtn.addActionListener(e -> handleDeleteUser());

        refreshBtn = new ModernButton("Refresh", false);
        refreshBtn.setPreferredSize(new Dimension(100, DesignConstants.BUTTON_HEIGHT));
        refreshBtn.addActionListener(e -> loadUsers());

        panel.add(addUserBtn);
        panel.add(deleteUserBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private void loadUsers() {
        tableModel.setRowCount(0);

        for (User user : userManager.getUsers()) {
            Object[] row = {
                    user.getUsername(),
                    user.getRole().toString(),
                    "Active"
            };
            tableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG
        ));
        panel.setBackground(DesignConstants.SURFACE);

        
        ModernTextField usernameField = new ModernTextField(20, "Username");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));

        
        ModernPasswordField passwordField = new ModernPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));

        
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(DesignConstants.FONT_BODY_BOLD);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] roles = {"Manager", "Supervisor"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(DesignConstants.FONT_BODY);
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(createDialogField("Username:", usernameField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Password:", passwordField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(roleLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        panel.add(roleCombo);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        ModernButton createButton = new ModernButton("Create User");
        createButton.setSuccess();
        createButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter username and password",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            UserRole role = roleCombo.getSelectedIndex() == 0 ? UserRole.MANAGER : UserRole.SUPERVISOR;
            User newUser = new User(username, password, role);

            userManager.addUser(newUser);

            JOptionPane.showMessageDialog(dialog,
                    "User created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            dialog.dispose();
            loadUsers();
        });

        ModernButton cancelButton = new ModernButton("Cancel", false);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void handleDeleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to delete",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(selectedRow, 0);
        String roleStr = (String) tableModel.getValueAt(selectedRow, 1);

        
        if (username.equals("admin")) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete admin user!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user: " + username + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserRole role = roleStr.equals("MANAGER") ? UserRole.MANAGER : UserRole.SUPERVISOR;

            
            User userToDelete = null;
            for (User user : userManager.getUsers()) {
                if (user.getUsername().equals(username) && user.getRole() == role) {
                    userToDelete = user;
                    break;
                }
            }

            if (userToDelete != null) {
                userManager.removeUser(userToDelete);
                JOptionPane.showMessageDialog(this,
                        "User deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            }
        }
    }

    private JPanel createDialogField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(DesignConstants.FONT_BODY_BOLD);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        panel.add(field);

        return panel;
    }
}