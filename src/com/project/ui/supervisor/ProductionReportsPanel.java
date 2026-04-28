package com.project.ui.supervisor;

import com.project.controller.InventoryManager;
import com.project.controller.ProductionManager;
import com.project.model.*;
import com.project.ui.DesignConstants;
import com.project.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;


public class ProductionReportsPanel extends JPanel {

    private InventoryManager inventoryManager;
    private ProductionManager productionManager;

    private JComboBox<String> reportTypeCombo;
    private ModernButton generateBtn;
    private JTextArea reportArea;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    public ProductionReportsPanel(InventoryManager inv, ProductionManager prod) {
        this.inventoryManager = inv;
        this.productionManager = prod;

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
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

        mainContent.add(createReportSelectionPanel(), BorderLayout.NORTH);
        mainContent.add(createReportDisplayPanel(), BorderLayout.CENTER);

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

        JLabel titleLabel = new JLabel("Production Reports");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Generate and view production analytics");
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

    private JPanel createReportSelectionPanel() {
        ModernPanel panel = new ModernPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, DesignConstants.SPACING_MD, DesignConstants.SPACING_MD));

        JLabel reportLabel = new JLabel("Select Report Type:");
        reportLabel.setFont(DesignConstants.FONT_BODY_BOLD);

        
        String[] reportTypes = {
                "Production Lines Overview",
                "Tasks by Production Line",
                "Tasks by Product",
                "Production Lines That Manufactured Specific Product", 
                "Most Requested Products (Last 30 Days)",
                "Products by Production Line",
                "Production Line Performance"
        };

        reportTypeCombo = new JComboBox<>(reportTypes);
        reportTypeCombo.setFont(DesignConstants.FONT_BODY);
        reportTypeCombo.setPreferredSize(new Dimension(350, DesignConstants.INPUT_HEIGHT));

        generateBtn = new ModernButton("Generate Report");
        generateBtn.setPreferredSize(new Dimension(150, DesignConstants.INPUT_HEIGHT));
        generateBtn.addActionListener(e -> generateReport());

        panel.add(reportLabel);
        panel.add(reportTypeCombo);
        panel.add(generateBtn);

        return panel;
    }

    private JPanel createReportDisplayPanel() {
        JPanel displayPanel = new JPanel(new BorderLayout(DesignConstants.SPACING_MD, DesignConstants.SPACING_MD));
        displayPanel.setBackground(DesignConstants.BACKGROUND);

        ModernPanel textPanel = new ModernPanel(new BorderLayout());
        textPanel.setPreferredSize(new Dimension(0, 200));

        JLabel textTitle = new JLabel("Report Summary");
        textTitle.setFont(DesignConstants.FONT_H3);
        textTitle.setForeground(DesignConstants.TEXT_PRIMARY);
        textTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.SPACING_SM, 0));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        reportArea.setBackground(DesignConstants.SURFACE_DARK);
        reportArea.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM,
                DesignConstants.SPACING_SM
        ));

        JScrollPane textScroll = new JScrollPane(reportArea);
        textScroll.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        textPanel.add(textTitle, BorderLayout.NORTH);
        textPanel.add(textScroll, BorderLayout.CENTER);

        ModernPanel tablePanel = new ModernPanel(new BorderLayout());

        JLabel tableTitle = new JLabel("Detailed Data");
        tableTitle.setFont(DesignConstants.FONT_H3);
        tableTitle.setForeground(DesignConstants.TEXT_PRIMARY);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, DesignConstants.SPACING_SM, 0));

        String[] columns = {"Column 1", "Column 2", "Column 3", "Column 4", "Column 5"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setFont(DesignConstants.FONT_BODY);
        reportTable.setRowHeight(35);
        reportTable.getTableHeader().setFont(DesignConstants.FONT_BODY_BOLD);
        reportTable.getTableHeader().setBackground(DesignConstants.SURFACE_DARK);
        reportTable.setGridColor(DesignConstants.BORDER_LIGHT);

        JScrollPane tableScroll = new JScrollPane(reportTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        displayPanel.add(textPanel, BorderLayout.NORTH);
        displayPanel.add(tablePanel, BorderLayout.CENTER);

        return displayPanel;
    }

    private void generateReport() {
        int reportType = reportTypeCombo.getSelectedIndex();

        switch (reportType) {
            case 0 -> generateProductionLinesOverview();
            case 1 -> generateTasksByProductionLine();
            case 2 -> generateTasksByProduct();
            case 3 -> generateLinesThatManufacturedProduct(); 
            case 4 -> generateMostRequestedProduct();
            case 5 -> generateProductsByProductionLine();
            case 6 -> generatePerformanceReport();
        }
    }

    private void generateProductionLinesOverview() {
        StringBuilder summary = new StringBuilder();
        summary.append("PRODUCTION LINES OVERVIEW REPORT\n");
        summary.append("Generated: ").append(LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        summary.append("═══════════════════════════════════════════════\n\n");

        int totalLines = inventoryManager.productsLines.size();
        long activeLines = inventoryManager.productsLines.values().stream()
                .filter(l -> l.getStatus() == LineStatus.ACTIVE).count();

        summary.append("Total Production Lines: ").append(totalLines).append("\n");
        summary.append("Active Lines: ").append(activeLines).append("\n");
        summary.append("Inactive Lines: ").append(totalLines - activeLines).append("\n\n");

        reportArea.setText(summary.toString());

        String[] columns = {"Line ID", "Name", "Status", "Pending Tasks", "Completed Tasks"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (ProductLine line : inventoryManager.productsLines.values()) {
            Object[] row = {
                    line.getId(),
                    line.getName(),
                    line.getStatus().toString(),
                    line.getTasks().size(),
                    line.getCompletedTasks().size()
            };
            tableModel.addRow(row);
        }
    }

    private void generateTasksByProductionLine() {
        Object[] lines = inventoryManager.productsLines.values().stream()
                .map(l -> l.getId() + " - " + l.getName())
                .toArray();

        if (lines.length == 0) {
            JOptionPane.showMessageDialog(this, "No production lines found", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select Production Line:",
                "Production Line Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                lines,
                lines[0]
        );

        if (selected == null) return;

        int lineId = Integer.parseInt(selected.split(" - ")[0]);
        ProductLine line = inventoryManager.getProductLineById(lineId);

        StringBuilder summary = new StringBuilder();
        summary.append("TASKS FOR PRODUCTION LINE: ").append(line.getName()).append("\n");
        summary.append("Line ID: ").append(line.getId()).append("\n");
        summary.append("Status: ").append(line.getStatus()).append("\n");
        summary.append("═══════════════════════════════════════════════\n\n");

        Queue<Task> tasks = productionManager.showTaskProductLine(lineId);
        summary.append("Pending Tasks: ").append(tasks.size()).append("\n");
        summary.append("Completed Tasks: ").append(line.getCompletedTasks().size()).append("\n\n");

        reportArea.setText(summary.toString());

        String[] columns = {"Task ID", "Product", "Customer", "Quantity", "Status", "Progress %"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (Task task : tasks) {
            Object[] row = {
                    task.getId(),
                    task.getProduct().getName(),
                    task.getCustomerName(),
                    task.getQuantityRequired(),
                    task.getStatus().toString(),
                    String.format("%.1f%%", task.getCompletPercent())
            };
            tableModel.addRow(row);
        }
    }

    private void generateTasksByProduct() {
        Object[] products = inventoryManager.products.values().stream()
                .map(p -> p.getId() + " - " + p.getName())
                .toArray();

        if (products.length == 0) {
            JOptionPane.showMessageDialog(this, "No products found", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select Product:",
                "Product Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                products,
                products[0]
        );

        if (selected == null) return;

        int productId = Integer.parseInt(selected.split(" - ")[0]);
        Product product = inventoryManager.getProductById(productId);

        List<Task> tasks = productionManager.showTasksForProduct(productId);

        StringBuilder summary = new StringBuilder();
        summary.append("TASKS FOR PRODUCT: ").append(product.getName()).append("\n");
        summary.append("Product ID: ").append(product.getId()).append("\n");
        summary.append("═══════════════════════════════════════════════\n\n");
        summary.append("Total Tasks: ").append(tasks.size()).append("\n");

        long completed = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();

        summary.append("Completed: ").append(completed).append("\n");
        summary.append("In Progress: ").append(inProgress).append("\n\n");

        reportArea.setText(summary.toString());

        String[] columns = {"Task ID", "Customer", "Quantity", "Status", "Assigned Line", "Progress %"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (Task task : tasks) {
            String lineName = task.getAssignedLine() != null ? task.getAssignedLine().getName() : "Not Assigned";
            Object[] row = {
                    task.getId(),
                    task.getCustomerName(),
                    task.getQuantityRequired(),
                    task.getStatus().toString(),
                    lineName,
                    String.format("%.1f%%", task.getCompletPercent())
            };
            tableModel.addRow(row);
        }
    }


    private void generateLinesThatManufacturedProduct() {
        Object[] products = inventoryManager.products.values().stream()
                .map(p -> p.getId() + " - " + p.getName())
                .toArray();

        if (products.length == 0) {
            JOptionPane.showMessageDialog(this, "No products found", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select Product to find which lines manufactured it:",
                "Product Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                products,
                products[0]
        );

        if (selected == null) return;

        int productId = Integer.parseInt(selected.split(" - ")[0]);
        Product product = inventoryManager.getProductById(productId);

        
        List<ProductLine> linesWithProduct = new ArrayList<>();
        Map<ProductLine, Integer> lineTaskCounts = new HashMap<>();

        for (ProductLine line : inventoryManager.productsLines.values()) {
            int taskCount = 0;
            for (Task task : line.getCompletedTasks()) {
                if (task.getProduct().getId() == productId) {
                    taskCount++;
                }
            }
            if (taskCount > 0) {
                linesWithProduct.add(line);
                lineTaskCounts.put(line, taskCount);
            }
        }

        StringBuilder summary = new StringBuilder();
        summary.append("PRODUCTION LINES FOR PRODUCT: ").append(product.getName()).append("\n");
        summary.append("Product ID: ").append(product.getId()).append("\n");
        summary.append("═══════════════════════════════════════════════\n\n");

        if (linesWithProduct.isEmpty()) {
            summary.append("No production lines have manufactured this product yet.\n");
        } else {
            summary.append("Lines That Manufactured This Product: ").append(linesWithProduct.size()).append("\n\n");

            int totalTasks = lineTaskCounts.values().stream().mapToInt(Integer::intValue).sum();
            summary.append("Total Completed Tasks: ").append(totalTasks).append("\n");
        }

        reportArea.setText(summary.toString());

        String[] columns = {"Line ID", "Line Name", "Status", "Tasks Completed", "Products Made", "Rating"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (ProductLine line : linesWithProduct) {
            int tasksForProduct = lineTaskCounts.get(line);

            Object[] row = {
                    line.getId(),
                    line.getName(),
                    line.getStatus().toString(),
                    tasksForProduct,
                    line.getProducts().size(),
                    line.getManagerRating()
            };
            tableModel.addRow(row);
        }

        if (linesWithProduct.isEmpty()) {
            tableModel.addRow(new Object[]{"N/A", "No data available", "-", "-", "-", "-"});
        }
    }

    private void generateMostRequestedProduct() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(30);

        Map<Product, Integer> productCounts = productionManager.mostProduct(start, end);

        StringBuilder summary = new StringBuilder();
        summary.append("MOST REQUESTED PRODUCTS (LAST 30 DAYS)\n");
        summary.append("Period: ").append(start.format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .append(" to ").append(end.format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
        summary.append("═══════════════════════════════════════════════\n\n");

        if (productCounts.isEmpty()) {
            summary.append("No products found in this period.\n");
        } else {
            summary.append("Total Products Tracked: ").append(productCounts.size()).append("\n\n");
        }

        reportArea.setText(summary.toString());

        String[] columns = {"Rank", "Product ID", "Product Name", "Times Requested", "Percentage"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        int totalRequests = productCounts.values().stream().mapToInt(Integer::intValue).sum();

        List<Map.Entry<Product, Integer>> sortedEntries = new ArrayList<>(productCounts.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        int rank = 1;
        for (Map.Entry<Product, Integer> entry : sortedEntries) {
            double percentage = (entry.getValue() * 100.0) / totalRequests;
            Object[] row = {
                    rank++,
                    entry.getKey().getId(),
                    entry.getKey().getName(),
                    entry.getValue(),
                    String.format("%.1f%%", percentage)
            };
            tableModel.addRow(row);
        }
    }

    private void generateProductsByProductionLine() {
        StringBuilder summary = new StringBuilder();
        summary.append("PRODUCTS MANUFACTURED BY PRODUCTION LINES\n");
        summary.append("═══════════════════════════════════════════════\n\n");

        Map<ProductLine, List<Product>> allProducts = productionManager.productForAllLine();

        int totalProducts = allProducts.values().stream()
                .mapToInt(List::size)
                .sum();

        summary.append("Total Unique Products: ").append(totalProducts).append("\n\n");

        reportArea.setText(summary.toString());

        String[] columns = {"Line ID", "Line Name", "Products Count", "Products"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (Map.Entry<ProductLine, List<Product>> entry : allProducts.entrySet()) {
            ProductLine line = entry.getKey();
            List<Product> products = entry.getValue();

            StringBuilder productNames = new StringBuilder();
            for (int i = 0; i < products.size(); i++) {
                if (i > 0) productNames.append(", ");
                productNames.append(products.get(i).getName());
            }

            Object[] row = {
                    line.getId(),
                    line.getName(),
                    products.size(),
                    productNames.toString()
            };
            tableModel.addRow(row);
        }
    }

    private void generatePerformanceReport() {
        StringBuilder summary = new StringBuilder();
        summary.append("PRODUCTION LINE PERFORMANCE REPORT\n");
        summary.append("Generated: ").append(LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        summary.append("═══════════════════════════════════════════════\n\n");

        int totalTasks = inventoryManager.tasks.size();
        long completedTasks = inventoryManager.tasks.values().stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();

        double overallEfficiency = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0;

        summary.append("Overall Completion Rate: ").append(String.format("%.1f%%", overallEfficiency)).append("\n");
        summary.append("Total Tasks: ").append(totalTasks).append("\n");
        summary.append("Completed Tasks: ").append(completedTasks).append("\n\n");

        reportArea.setText(summary.toString());

        String[] columns = {"Line ID", "Name", "Rating", "Completed", "Products Made", "Current Progress %"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (ProductLine line : inventoryManager.productsLines.values()) {
            Object[] row = {
                    line.getId(),
                    line.getName(),
                    line.getManagerRating(),
                    line.getCompletedTasks().size(),
                    line.getProducts().size(),
                    String.format("%.1f%%", line.getCompletPercentForCurrentTask())
            };
            tableModel.addRow(row);
        }
    }
}