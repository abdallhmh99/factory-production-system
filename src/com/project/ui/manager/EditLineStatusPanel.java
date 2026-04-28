package com.project.ui.manager;

import com.project.controller.InventoryManager;
import com.project.controller.ProductionManager;
import com.project.controller.ThreadManager;
import com.project.model.LineStatus;
import com.project.model.ProductLine;
import com.project.ui.DesignConstants;
import com.project.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class EditLineStatusPanel extends JPanel {

    private Timer refreshTimer;
    private InventoryManager inventoryManager;
    private ProductionManager productionManager;

    private JTable linesTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusComboBox;
    private ModernButton updateButton;
    private ModernButton refreshButton;
    private ModernButton checkThreadsBtn;
    private JLabel selectedLineLabel;
    private JLabel threadStatusLabel;

    public EditLineStatusPanel(InventoryManager inv, ProductionManager prod) {
        this.inventoryManager = inv;
        this.productionManager = prod;

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
        loadProductionLines();
        startAutoRefresh();
    }

    private boolean isEditing = false;

    private void startAutoRefresh() {
        refreshTimer = new Timer(3000, e -> {
            if (!isEditing) {
                loadProductionLines();
                updateThreadStatus();
            }
        });
        refreshTimer.start();
    }

    private void initializeUI() {
        add(createHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(DesignConstants.SPACING_LG, DesignConstants.SPACING_LG));
        contentPanel.setBackground(DesignConstants.BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG
        ));

        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        contentPanel.add(createEditPanel(), BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
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

        JLabel titleLabel = new JLabel("Edit Production Line Status");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Update status of existing production lines");
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

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.SPACING_MD, 0));

        JLabel tableTitle = new JLabel("Production Lines");
        tableTitle.setFont(DesignConstants.FONT_H3);
        tableTitle.setForeground(DesignConstants.TEXT_PRIMARY);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.SPACING_SM, 0));
        buttonPanel.setOpaque(false);

        
        checkThreadsBtn = new ModernButton("Thread Status", false);
        checkThreadsBtn.setPreferredSize(new Dimension(130, 35));
        checkThreadsBtn.addActionListener(e -> showThreadStatusDialog());

        refreshButton = new ModernButton("Refresh", false);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadProductionLines());

        buttonPanel.add(checkThreadsBtn);
        buttonPanel.add(refreshButton);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(buttonPanel, BorderLayout.EAST);

        String[] columns = {"ID", "Name", "Status", "Thread", "Pending Tasks", "Completed Tasks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        linesTable = new JTable(tableModel);
        linesTable.setFont(DesignConstants.FONT_BODY);
        linesTable.setRowHeight(40);
        linesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linesTable.getTableHeader().setFont(DesignConstants.FONT_BODY_BOLD);
        linesTable.getTableHeader().setBackground(DesignConstants.SURFACE_DARK);
        linesTable.setGridColor(DesignConstants.BORDER_LIGHT);

        linesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedLine();
            }
        });

        JScrollPane scrollPane = new JScrollPane(linesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        panel.add(tableHeader, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    
    private void showThreadStatusDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thread Status Report", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(DesignConstants.SPACING_MD, DesignConstants.SPACING_MD));
        panel.setBackground(DesignConstants.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG
        ));

        JLabel titleLabel = new JLabel("Production Line Thread Status");
        titleLabel.setFont(DesignConstants.FONT_H2);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        statusArea.setBackground(DesignConstants.SURFACE_DARK);
        statusArea.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM
        ));

        
        StringBuilder report = new StringBuilder();
        report.append("========== THREAD STATUS REPORT ==========\n");
        report.append("Generated: ").append(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");

        for (ProductLine line : inventoryManager.productsLines.values()) {
            String threadStatus = ThreadManager.getInstance().getThreadStatus(line.getId());

            report.append(String.format("Line %d: %s\n", line.getId(), line.getName()));
            report.append(String.format("  Status: %s\n", line.getStatus()));
            report.append(String.format("  Thread: %s\n", threadStatus));
            report.append(String.format("  Pending Tasks: %d\n", line.getTasks().size()));
            report.append(String.format("  Progress: %.1f%%\n", line.getCompletPercentForCurrentTask()));

            if (threadStatus.equals("Dead") && line.getStatus() == LineStatus.ACTIVE) {
                report.append("  ⚠️ WARNING: Line is ACTIVE but thread is DEAD!\n");
            }
            report.append("\n");
        }

        report.append("==========================================\n");

        statusArea.setText(report.toString());
        statusArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        ModernButton closeBtn = new ModernButton("Close", false);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeBtn);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private JPanel createEditPanel() {
        ModernPanel panel = new ModernPanel();
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Update Status");
        titleLabel.setFont(DesignConstants.FONT_H3);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        selectedLineLabel = new JLabel("No line selected");
        selectedLineLabel.setFont(DesignConstants.FONT_BODY);
        selectedLineLabel.setForeground(DesignConstants.TEXT_SECONDARY);
        selectedLineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        threadStatusLabel = new JLabel("Thread: Not checked");
        threadStatusLabel.setFont(DesignConstants.FONT_SMALL);
        threadStatusLabel.setForeground(DesignConstants.TEXT_MUTED);
        threadStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statusGroup = new JPanel();
        statusGroup.setOpaque(false);
        statusGroup.setLayout(new BoxLayout(statusGroup, BoxLayout.Y_AXIS));
        statusGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusLabel = new JLabel("New Status");
        statusLabel.setFont(DesignConstants.FONT_BODY_BOLD);
        statusLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] statuses = {"Active", "Stopped", "Maintenance"};
        statusComboBox = new JComboBox<>(statuses);
        statusComboBox.setFont(DesignConstants.FONT_BODY);
        statusComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        statusComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusGroup.add(statusLabel);
        statusGroup.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        statusGroup.add(statusComboBox);

        
        updateButton = new ModernButton("Apply Changes");
        updateButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.BUTTON_HEIGHT));
        updateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateButton.setEnabled(false);
        updateButton.setSuccess();
        updateButton.addActionListener(e -> handleSmartUpdate());

        JPanel infoPanel = createInfoPanel();

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(selectedLineLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        panel.add(threadStatusLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XL));
        panel.add(statusGroup);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));
        panel.add(updateButton);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XL));
        panel.add(infoPanel);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DesignConstants.ACCENT, 1),
                BorderFactory.createEmptyBorder(
                        DesignConstants.SPACING_MD,
                        DesignConstants.SPACING_MD,
                        DesignConstants.SPACING_MD,
                        DesignConstants.SPACING_MD
                )
        ));

        JLabel infoTitle = new JLabel("Status Information");
        infoTitle.setFont(DesignConstants.FONT_BODY_BOLD);
        infoTitle.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel info1 = new JLabel("• Active: Line is running");
        JLabel info2 = new JLabel("• Stopped: Line is paused");
        JLabel info3 = new JLabel("• Maintenance: Under repair");
        JLabel info4 = new JLabel("• Auto thread restart if needed");

        info1.setFont(DesignConstants.FONT_SMALL);
        info2.setFont(DesignConstants.FONT_SMALL);
        info3.setFont(DesignConstants.FONT_SMALL);
        info4.setFont(DesignConstants.FONT_SMALL);

        info1.setForeground(DesignConstants.TEXT_SECONDARY);
        info2.setForeground(DesignConstants.TEXT_SECONDARY);
        info3.setForeground(DesignConstants.TEXT_SECONDARY);
        info4.setForeground(DesignConstants.TEXT_SECONDARY);

        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        infoPanel.add(info1);
        infoPanel.add(info2);
        infoPanel.add(info3);
        infoPanel.add(info4);

        return infoPanel;
    }

    private void loadProductionLines() {
        tableModel.setRowCount(0);

        for (ProductLine line : inventoryManager.productsLines.values()) {
            String threadStatus = ThreadManager.getInstance().getThreadStatus(line.getId());

            Object[] row = {
                    line.getId(),
                    line.getName(),
                    line.getStatus().toString(),
                    threadStatus,
                    line.getTasks().size(),
                    line.getCompletedTasks().size()
            };
            tableModel.addRow(row);
        }
    }

    private void updateSelectedLine() {
        int selectedRow = linesTable.getSelectedRow();

        if (selectedRow >= 0) {
            isEditing = true;

            int lineId = (int) tableModel.getValueAt(selectedRow, 0);
            String lineName = (String) tableModel.getValueAt(selectedRow, 1);
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 2);
            String threadStatus = (String) tableModel.getValueAt(selectedRow, 3);

            selectedLineLabel.setText("Selected: " + lineName + " (ID: " + lineId + ")");
            threadStatusLabel.setText("Thread: " + threadStatus);

            updateButton.setEnabled(true);

            switch (currentStatus) {
                case "active" -> statusComboBox.setSelectedIndex(0);
                case "stopped" -> statusComboBox.setSelectedIndex(1);
                case "MAINTENANCE" -> statusComboBox.setSelectedIndex(2);
            }
        } else {
            isEditing = false;
            selectedLineLabel.setText("No line selected");
            threadStatusLabel.setText("Thread: Not checked");
            updateButton.setEnabled(false);
        }
    }

    private void updateThreadStatus() {
        int selectedRow = linesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int lineId = (int) tableModel.getValueAt(selectedRow, 0);
            String threadStatus = ThreadManager.getInstance().getThreadStatus(lineId);
            threadStatusLabel.setText("Thread: " + threadStatus);
        }
    }

    
    private void handleSmartUpdate() {
        int selectedRow = linesTable.getSelectedRow();
        if (selectedRow < 0) return;

        int lineId = (int) tableModel.getValueAt(selectedRow, 0);
        String lineName = (String) tableModel.getValueAt(selectedRow, 1);
        String currentThreadStatus = ThreadManager.getInstance().getThreadStatus(lineId);

        LineStatus newStatus = switch (statusComboBox.getSelectedIndex()) {
            case 0 -> LineStatus.ACTIVE;
            case 1 -> LineStatus.STOPPED;
            case 2 -> LineStatus.MAINTENANCE;
            default -> LineStatus.ACTIVE;
        };

        ProductLine line = inventoryManager.getProductLineById(lineId);
        boolean needsThreadRestart = false;

        
        if (newStatus == LineStatus.ACTIVE &&
                (currentThreadStatus.equals("Dead") || currentThreadStatus.equals("Not Started"))) {
            needsThreadRestart = true;
        }

        
        StringBuilder message = new StringBuilder();
        message.append("Apply the following changes?\n\n");
        message.append("Line: ").append(lineName).append("\n");
        message.append("New Status: ").append(newStatus).append("\n");
        message.append("Current Thread: ").append(currentThreadStatus).append("\n\n");

        if (needsThreadRestart) {
            message.append("⚠️ Thread will be restarted automatically\n");
            message.append("(Thread was ").append(currentThreadStatus).append(")\n");
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                message.toString(),
                "Confirm Changes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            
            boolean success = productionManager.changeLineState(lineId, newStatus);

            if (success) {
                
                if (newStatus == LineStatus.ACTIVE) {
                    if (needsThreadRestart) {
                        System.out.println("🔄 Restarting thread for: " + lineName);
                        ThreadManager.getInstance().restartLineThread(line);
                    } else {
                        System.out.println("✅ Ensuring thread is running for: " + lineName);
                        ThreadManager.getInstance().startLineThread(line);
                    }
                }

                String resultMessage = "Status updated successfully!\n\n" +
                        "Line: " + lineName + "\n" +
                        "New Status: " + newStatus;

                if (needsThreadRestart) {
                    resultMessage += "\n\n✅ Thread restarted successfully";
                }

                JOptionPane.showMessageDialog(this,
                        resultMessage,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                isEditing = false;
                loadProductionLines();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update status",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}