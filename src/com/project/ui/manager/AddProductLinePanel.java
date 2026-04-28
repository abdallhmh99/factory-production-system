package com.project.ui.manager;

import com.project.controller.InventoryManager;
import com.project.controller.ProductionManager;
import com.project.controller.ThreadManager;
import com.project.model.LineStatus;
import com.project.model.ProductLine;
import com.project.ui.DesignConstants;
import com.project.ui.components.*;
import javax.swing.*;
import java.awt.*;


public class AddProductLinePanel extends JPanel {

    private InventoryManager inventoryManager;
    private ProductionManager productionManager;

    private ModernTextField lineNameField;
    private JComboBox<String> statusComboBox;
    private ModernButton addButton;
    private ModernButton clearButton;
    private JTextArea logArea;

    public AddProductLinePanel(InventoryManager inv, ProductionManager prod) {
        this.inventoryManager = inv;
        this.productionManager = prod;

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
    }

    private void initializeUI() {
        
        add(createHeader(), BorderLayout.NORTH);

        
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(DesignConstants.BACKGROUND);
        formContainer.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG
        ));

        ModernPanel formPanel = createFormPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        formContainer.add(formPanel, gbc);

        add(formContainer, BorderLayout.CENTER);
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

        JLabel titleLabel = new JLabel("Add New Production Line");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Create a new production line for the system");
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

    private ModernPanel createFormPanel() {
        ModernPanel panel = new ModernPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(600, 500));

        
        panel.add(createFieldGroup("Line Name",
                lineNameField = new ModernTextField(30, "e.g., Production Line A")));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        
        panel.add(createStatusGroup());
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XL));

        
        panel.add(createButtonPanel());
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        
        panel.add(createLogPanel());

        return panel;
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
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

    private JPanel createStatusGroup() {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("Initial Status");
        label.setFont(DesignConstants.FONT_BODY_BOLD);
        label.setForeground(DesignConstants.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] statuses = {"Active", "Stopped", "Maintenance"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setFont(DesignConstants.FONT_BODY);
        statusComboBox.setBackground(DesignConstants.SURFACE);
        statusComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        statusComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(label);
        group.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        group.add(statusComboBox);

        return group;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, DesignConstants.SPACING_MD, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addButton = new ModernButton("Add Production Line");
        addButton.setPreferredSize(new Dimension(180, DesignConstants.BUTTON_HEIGHT));
        addButton.setSuccess();
        addButton.addActionListener(e -> handleAddLine());

        clearButton = new ModernButton("Clear", false);
        clearButton.setPreferredSize(new Dimension(100, DesignConstants.BUTTON_HEIGHT));
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        return buttonPanel;
    }

    private JPanel createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setOpaque(false);
        logPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logLabel = new JLabel("Activity Log");
        logLabel.setFont(DesignConstants.FONT_BODY_BOLD);
        logLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setFont(DesignConstants.FONT_SMALL);
        logArea.setBackground(DesignConstants.SURFACE_DARK);
        logArea.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM
        ));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        logPanel.add(logLabel, BorderLayout.NORTH);
        logPanel.add(Box.createVerticalStrut(DesignConstants.SPACING_SM), BorderLayout.AFTER_LINE_ENDS);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        return logPanel;
    }

    private void handleAddLine() {
        String lineName = lineNameField.getText().trim();

        if (lineName.isEmpty()) {
            showError("Please enter a line name");
            return;
        }

        
        LineStatus status = switch (statusComboBox.getSelectedIndex()) {
            case 0 -> LineStatus.ACTIVE;
            case 1 -> LineStatus.STOPPED;
            case 2 -> LineStatus.MAINTENANCE;
            default -> LineStatus.ACTIVE;
        };

        
        int newId = inventoryManager.generateProductLineId();

        
        ProductLine newLine = new ProductLine(newId, lineName, status);
        inventoryManager.addProductLine(newLine);

        
        if (status == LineStatus.ACTIVE) {
            ThreadManager.getInstance().startLineThread(newLine);
        }

        
        appendLog("SUCCESS: Production line '" + lineName + "' added with ID: " + newId);
        appendLog("Status: " + status + " | Thread: " + (status == LineStatus.ACTIVE ? "Started" : "Not started"));

        
        JOptionPane.showMessageDialog(this,
                "Production line added successfully!\nID: " + newId + "\nName: " + lineName,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        
        clearForm();
    }

    private void clearForm() {
        lineNameField.setText("");
        statusComboBox.setSelectedIndex(0);
    }

    private void appendLog(String message) {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}