package com.project.ui.supervisor;

import com.project.controller.FileManager;
import com.project.controller.InventoryManager;
import com.project.controller.ProductionManager;
import com.project.controller.ThreadManager;
import com.project.model.*;
import com.project.ui.DesignConstants;
import com.project.ui.components.ModernButton;
import com.project.ui.components.ModernPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class SupervisorOverviewPanel extends JPanel {

    private InventoryManager inventoryManager;
    private ProductionManager productionManager;
    private FileManager fileManager;
    private Timer refreshTimer;
    private JPanel statsPanel;
    private JTable linesTable;
    private DefaultTableModel linesTableModel;

    public SupervisorOverviewPanel(InventoryManager inv, ProductionManager prod, FileManager fm) {
        this.inventoryManager = inv;
        this.productionManager = prod;
        this.fileManager = fm;

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(2000, e -> {
            refreshStats();
            refreshLinesTable();
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

    private void refreshLinesTable() {
        linesTableModel.setRowCount(0);

        for (ProductLine line : inventoryManager.productsLines.values()) {
            String threadStatus = ThreadManager.getInstance().getThreadStatus(line.getId());

            Object[] row = {
                    line.getId(),
                    line.getName(),
                    line.getStatus().toString(),
                    threadStatus,
                    line.getTasks().size(),
                    line.getCompletedTasks().size(),
                    String.format("%.1f%%", line.getCompletPercentForCurrentTask())
            };
            linesTableModel.addRow(row);
        }
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

        JPanel centerPanel = new JPanel(new BorderLayout(DesignConstants.SPACING_LG, DesignConstants.SPACING_LG));
        centerPanel.setBackground(DesignConstants.BACKGROUND);

        centerPanel.add(createProductionLinesTable(), BorderLayout.CENTER);
        centerPanel.add(createQuickActionsPanel(), BorderLayout.SOUTH);

        mainContent.add(centerPanel, BorderLayout.CENTER);

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

        JLabel titleLabel = new JLabel("Supervisor Overview");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Production management and monitoring (Auto-refresh: 2s)");
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

        
        int totalItems = inventoryManager.items.size();
        long lowStockItems = inventoryManager.items.values().stream()
                .filter(i -> i.getQuantity() < i.getMinLimit())
                .count();

        int totalProducts = inventoryManager.products.size();

        int totalTasks = inventoryManager.tasks.size();
        long activeTasks = inventoryManager.tasks.values().stream()
                .filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS || t.getStatus() == TaskStatus.PENDING)
                .count();

        int totalLines = inventoryManager.productsLines.size();
        long activeLines = inventoryManager.productsLines.values().stream()
                .filter(l -> l.getStatus() == LineStatus.ACTIVE)
                .count();

        
        statsPanel.add(createStatCard("Inventory Items", String.valueOf(totalItems),
                lowStockItems + " Low Stock", DesignConstants.PRIMARY));
        statsPanel.add(createStatCard("Products", String.valueOf(totalProducts),
                "In Catalog", DesignConstants.ACCENT));
        statsPanel.add(createStatCard("Active Tasks", String.valueOf(activeTasks),
                "In Progress", DesignConstants.WARNING));
        statsPanel.add(createStatCard("Total Tasks", String.valueOf(totalTasks),
                "All Time", DesignConstants.SUCCESS));
        statsPanel.add(createStatCard("Production Lines", String.valueOf(totalLines),
                activeLines + " Active", DesignConstants.PRIMARY_LIGHT));

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

    
    private JPanel createProductionLinesTable() {
        ModernPanel panel = new ModernPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Production Lines - Live Status");
        titleLabel.setFont(DesignConstants.FONT_H3);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.SPACING_MD, 0));

        String[] columns = {"Line ID", "Name", "Status", "Thread", "Pending", "Completed", "Progress %"};
        linesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        linesTable = new JTable(linesTableModel);
        linesTable.setFont(DesignConstants.FONT_BODY);
        linesTable.setRowHeight(40);
        linesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        linesTable.getTableHeader().setFont(DesignConstants.FONT_BODY_BOLD);
        linesTable.getTableHeader().setBackground(DesignConstants.SURFACE_DARK);
        linesTable.setGridColor(DesignConstants.BORDER_LIGHT);

        
        linesTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        linesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        linesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        linesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        linesTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        linesTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        linesTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        
        refreshLinesTable();

        JScrollPane scrollPane = new JScrollPane(linesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    
    private JPanel createQuickActionsPanel() {
        ModernPanel panel = new ModernPanel();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(DesignConstants.FONT_H3);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.SPACING_LG, 0));

        JPanel actionsGrid = new JPanel(new GridLayout(1, 4, DesignConstants.SPACING_LG, DesignConstants.SPACING_LG));
        actionsGrid.setOpaque(false);

        
        ModernButton generateReportBtn = new ModernButton("Generate Report");
        generateReportBtn.setPreferredSize(new Dimension(200, 60));
        generateReportBtn.addActionListener(e -> {
            String path = fileManager.writeInventory();
            if (path != null) {
                JOptionPane.showMessageDialog(this, "Report generated:\n" + path, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        
        ModernButton lowStockBtn = new ModernButton("View Low Stock");
        lowStockBtn.setPreferredSize(new Dimension(200, 60));
        lowStockBtn.addActionListener(e -> showLowStockItems());

        
        ModernButton activeTasksBtn = new ModernButton("Active Tasks");
        activeTasksBtn.setPreferredSize(new Dimension(200, 60));
        activeTasksBtn.addActionListener(e -> showActiveTasks());

        
        ModernButton refreshBtn = new ModernButton("Refresh Now", false);
        refreshBtn.setPreferredSize(new Dimension(200, 60));
        refreshBtn.addActionListener(e -> {
            refreshStats();
            refreshLinesTable();
            JOptionPane.showMessageDialog(this, "Data refreshed!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        actionsGrid.add(generateReportBtn);
        actionsGrid.add(lowStockBtn);
        actionsGrid.add(activeTasksBtn);
        actionsGrid.add(refreshBtn);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(actionsGrid, BorderLayout.CENTER);

        return panel;
    }

    private void showLowStockItems() {
        java.util.List<Item> lowStock = inventoryManager.searchItemByminquantity();

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
        java.util.List<Task> active = productionManager.inProgressTask();

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
}