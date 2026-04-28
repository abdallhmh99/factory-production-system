package com.project.ui.manager;

import com.project.controller.FileManager;
import com.project.controller.InventoryManager;
import com.project.controller.ProductionManager;
import com.project.model.*;
import com.project.ui.DesignConstants;
import com.project.ui.components.ModernButton;
import com.project.ui.components.ModernPanel;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


public class OverviewPanel extends JPanel {

    private InventoryManager inventoryManager;
    private ProductionManager productionManager;
    private FileManager fileManager;
    private Timer refreshTimer;
    private JPanel statsPanel;
    private JTextArea messagesArea;

    public OverviewPanel(InventoryManager inv, ProductionManager prod, FileManager fm) {
        this.inventoryManager = inv;
        this.productionManager = prod;
        this.fileManager = fm;

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
        startAutoRefresh();
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

        statsPanel = createStatsPanel();
        mainContent.add(statsPanel, BorderLayout.NORTH);

        
        mainContent.add(createMessagesAndActionsPanel(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(2000, e -> {
            refreshStats();
            updateMessages();
        });
        refreshTimer.start();
    }

    private void refreshStats() {
        JPanel newStatsPanel = createStatsPanel();

        Container parent = statsPanel.getParent();
        if (parent != null) {
            parent.remove(statsPanel);
            parent.add(newStatsPanel, BorderLayout.NORTH);
            statsPanel = newStatsPanel;
            parent.revalidate();
            parent.repaint();
        }
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

        JLabel titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("System statistics and important alerts (Auto-refresh: 2s)");
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

    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, DesignConstants.SPACING_LG, DesignConstants.SPACING_LG));
        statsPanel.setBackground(DesignConstants.BACKGROUND);

        int totalLines = inventoryManager.productsLines.size();
        long activeLines = inventoryManager.productsLines.values().stream()
                .filter(l -> l.getStatus() == LineStatus.ACTIVE)
                .count();

        int totalTasks = inventoryManager.tasks.size();
        long completedTasks = inventoryManager.tasks.values().stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .count();

        int totalItems = inventoryManager.items.size();
        long lowStockItems = inventoryManager.items.values().stream()
                .filter(i -> i.getQuantity() < i.getMinLimit())
                .count();

        int totalProducts = inventoryManager.products.size();

        double efficiency = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;

        statsPanel.add(createStatCard("Production Lines", String.valueOf(totalLines),
                activeLines + " Active", DesignConstants.PRIMARY));
        statsPanel.add(createStatCard("Total Tasks", String.valueOf(totalTasks),
                completedTasks + " Completed", DesignConstants.ACCENT));
        statsPanel.add(createStatCard("Inventory Items", String.valueOf(totalItems),
                lowStockItems + " Low Stock", lowStockItems > 0 ? DesignConstants.WARNING : DesignConstants.SUCCESS));
        statsPanel.add(createStatCard("Products", String.valueOf(totalProducts),
                "In Catalog", DesignConstants.SUCCESS));
        statsPanel.add(createStatCard("Task Efficiency", String.format("%.1f%%", efficiency),
                "Completion Rate", efficiency > 70 ? DesignConstants.SUCCESS : DesignConstants.WARNING));

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, String subtitle, Color accentColor) {
        ModernPanel card = new ModernPanel();
        card.setLayout(new BorderLayout());

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(4, 0));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_MD,
                DesignConstants.SPACING_MD,
                DesignConstants.SPACING_MD,
                DesignConstants.SPACING_MD
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(DesignConstants.FONT_SMALL);
        titleLabel.setForeground(DesignConstants.TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(DesignConstants.FONT_SMALL);
        subtitleLabel.setForeground(DesignConstants.TEXT_MUTED);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        content.add(valueLabel);
        content.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        content.add(subtitleLabel);

        card.add(accentBar, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    
    private JPanel createMessagesAndActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, DesignConstants.SPACING_LG, 0));
        panel.setBackground(DesignConstants.BACKGROUND);

        panel.add(createMessagesPanel());
        panel.add(createQuickActionsPanel());

        return panel;
    }

    
    private JPanel createMessagesPanel() {
        ModernPanel panel = new ModernPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Important Alerts");
        titleLabel.setFont(DesignConstants.FONT_H3);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.SPACING_MD, 0));

        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        messagesArea.setBackground(DesignConstants.SURFACE_DARK);
        messagesArea.setForeground(DesignConstants.TEXT_PRIMARY);
        messagesArea.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM
        ));
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(messagesArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        updateMessages();

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    
    private void updateMessages() {
        StringBuilder messages = new StringBuilder();
        messages.append("SYSTEM ALERTS\n");
        messages.append("Updated: ").append(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))).append("\n");
        messages.append("═══════════════════════════════════════\n\n");

        boolean hasAlerts = false;

        
        List<Item> lowStock = inventoryManager.searchItemByminquantity();
        if (!lowStock.isEmpty()) {
            hasAlerts = true;
            messages.append("⚠️  LOW STOCK ITEMS (").append(lowStock.size()).append("):\n");
            for (Item item : lowStock) {
                messages.append("   • ").append(item.getName())
                        .append(" - Current: ").append(item.getQuantity())
                        .append(" / Min: ").append(item.getMinLimit()).append("\n");
            }
            messages.append("\n");
        }

        
        List<Item> outOfStock = inventoryManager.searchItemByquantity(0);
        if (!outOfStock.isEmpty()) {
            hasAlerts = true;
            messages.append("🔴 OUT OF STOCK ITEMS (").append(outOfStock.size()).append("):\n");
            for (Item item : outOfStock) {
                messages.append("   • ").append(item.getName()).append("\n");
            }
            messages.append("\n");
        }

        
        for (ProductLine line : inventoryManager.productsLines.values()) {
            if (line.getStatus() != LineStatus.ACTIVE && !line.getTasks().isEmpty()) {
                hasAlerts = true;
                messages.append("⏸️  INACTIVE LINE WITH PENDING TASKS:\n");
                messages.append("   • ").append(line.getName())
                        .append(" - Status: ").append(line.getStatus())
                        .append(" - Pending: ").append(line.getTasks().size()).append("\n\n");
            }
        }

        
        File errorFile = new File("error.txt");
        if (errorFile.exists()) {
            try {
                List<String> lines = Files.readAllLines(errorFile.toPath());
                int recentErrors = 0;
                String today = java.time.LocalDate.now().toString();

                for (int i = lines.size() - 1; i >= 0 && i >= lines.size() - 10; i--) {
                    if (lines.get(i).contains(today)) {
                        recentErrors++;
                    }
                }

                if (recentErrors > 0) {
                    hasAlerts = true;
                    messages.append("📋 RECENT ERRORS: ").append(recentErrors)
                            .append(" error(s) logged today\n");
                    messages.append("   Click 'View Error Log' for details\n\n");
                }
            } catch (IOException e) {
                
            }
        }

        if (!hasAlerts) {
            messages.append("✅ All systems operating normally\n");
            messages.append("   No alerts at this time\n");
        }

        messagesArea.setText(messages.toString());
        messagesArea.setCaretPosition(0);
    }

    
    private JPanel createQuickActionsPanel() {
        ModernPanel panel = new ModernPanel();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(DesignConstants.FONT_H3);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.SPACING_LG, 0));

        JPanel actionsGrid = new JPanel(new GridLayout(3, 2, DesignConstants.SPACING_LG, DesignConstants.SPACING_LG));
        actionsGrid.setOpaque(false);

        
        ModernButton generateReportBtn = new ModernButton("Inventory Report");
        generateReportBtn.setPreferredSize(new Dimension(200, 60));
        generateReportBtn.setSuccess();
        generateReportBtn.addActionListener(e -> {
            String path = fileManager.writeInventory();
            if (path != null) {
                JOptionPane.showMessageDialog(this,
                        "Inventory Report Generated!\n\nSaved to:\n" + path,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to generate report",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        
        ModernButton errorLogBtn = new ModernButton("View Error Log");
        errorLogBtn.setPreferredSize(new Dimension(200, 60));
        errorLogBtn.addActionListener(e -> showErrorLogDialog());

        
        ModernButton lowStockBtn = new ModernButton("View Low Stock");
        lowStockBtn.setPreferredSize(new Dimension(200, 60));
        lowStockBtn.addActionListener(e -> showLowStockItems());

        
        ModernButton activeTasksBtn = new ModernButton("Active Tasks");
        activeTasksBtn.setPreferredSize(new Dimension(200, 60));
        activeTasksBtn.addActionListener(e -> showActiveTasks());

        
        ModernButton linesStatusBtn = new ModernButton("Lines Status");
        linesStatusBtn.setPreferredSize(new Dimension(200, 60));
        linesStatusBtn.addActionListener(e -> showLinesStatus());

        
        ModernButton refreshBtn = new ModernButton("Refresh Now", false);
        refreshBtn.setPreferredSize(new Dimension(200, 60));
        refreshBtn.addActionListener(e -> {
            refreshStats();
            updateMessages();
            JOptionPane.showMessageDialog(this, "Data refreshed!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        actionsGrid.add(generateReportBtn);
        actionsGrid.add(errorLogBtn);
        actionsGrid.add(lowStockBtn);
        actionsGrid.add(activeTasksBtn);
        actionsGrid.add(linesStatusBtn);
        actionsGrid.add(refreshBtn);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(actionsGrid, BorderLayout.CENTER);

        return panel;
    }

    
    private void showErrorLogDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "System Error Log", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(DesignConstants.SPACING_MD, DesignConstants.SPACING_MD));
        panel.setBackground(DesignConstants.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG
        ));

        JLabel titleLabel = new JLabel("System Error Log");
        titleLabel.setFont(DesignConstants.FONT_H2);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(DesignConstants.SURFACE_DARK);
        logArea.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM
        ));

        
        File errorFile = new File("error.txt");
        if (errorFile.exists()) {
            try {
                String content = new String(Files.readAllBytes(errorFile.toPath()));
                if (content.trim().isEmpty()) {
                    logArea.setText("No errors logged.\n\nThe system is operating without errors.");
                } else {
                    logArea.setText(content);
                }
            } catch (IOException e) {
                logArea.setText("Error reading log file: " + e.getMessage());
            }
        } else {
            logArea.setText("Error log file not found.\n\nNo errors have been logged yet.");
        }

        logArea.setCaretPosition(logArea.getDocument().getLength()); 

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        ModernButton clearLogBtn = new ModernButton("Clear Log");
        clearLogBtn.setDanger();
        clearLogBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to clear the error log?",
                    "Confirm Clear",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Files.write(errorFile.toPath(), new byte[0]);
                    logArea.setText("Error log cleared.\n\nNo errors logged.");
                    JOptionPane.showMessageDialog(dialog, "Error log cleared successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Failed to clear log: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        ModernButton closeBtn = new ModernButton("Close", false);
        closeBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(clearLogBtn);
        buttonPanel.add(closeBtn);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showLowStockItems() {
        List<Item> lowStock = inventoryManager.searchItemByminquantity();

        if (lowStock.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No low stock items!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder message = new StringBuilder("Low Stock Items:\n\n");
            for (Item item : lowStock) {
                message.append(String.format("• %s - Current: %d / Min: %d\n",
                        item.getName(), item.getQuantity(), item.getMinLimit()));
            }
            JOptionPane.showMessageDialog(this, message.toString(), "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showActiveTasks() {
        List<Task> active = productionManager.inProgressTask();

        if (active.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active tasks!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder message = new StringBuilder("Active Tasks:\n\n");
            for (Task task : active) {
                message.append(String.format("• Task %d: %s (%.1f%% complete)\n",
                        task.getId(), task.getProduct().getName(), task.getCompletPercent()));
            }
            JOptionPane.showMessageDialog(this, message.toString(), "Active Tasks", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showLinesStatus() {
        StringBuilder message = new StringBuilder("Production Lines Status:\n\n");

        for (ProductLine line : inventoryManager.productsLines.values()) {
            message.append(String.format("• %s (ID: %d)\n", line.getName(), line.getId()));
            message.append(String.format("  Status: %s\n", line.getStatus()));
            message.append(String.format("  Pending Tasks: %d\n", line.getTasks().size()));
            message.append(String.format("  Completed: %d\n", line.getCompletedTasks().size()));
            message.append(String.format("  Progress: %.1f%%\n\n", line.getCompletPercentForCurrentTask()));
        }

        JOptionPane.showMessageDialog(this, message.toString(), "Production Lines Status", JOptionPane.INFORMATION_MESSAGE);
    }
}