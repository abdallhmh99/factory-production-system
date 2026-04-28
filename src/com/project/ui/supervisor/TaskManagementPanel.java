package com.project.ui.supervisor;

import com.project.MyExceptions;
import com.project.controller.InventoryManager;
import com.project.controller.ProductionManager;
import com.project.model.*;
import com.project.ui.DesignConstants;
import com.project.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TaskManagementPanel extends JPanel {

    private InventoryManager inventoryManager;
    private ProductionManager productionManager;

    private JTable tasksTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> filterCombo;
    private ModernButton addTaskBtn;
    private ModernButton cancelTaskBtn;
    private ModernButton refreshBtn;
    private ModernButton viewDetailsBtn;
    private Timer refreshTimer;

    public TaskManagementPanel(InventoryManager inv, ProductionManager prod) {
        this.inventoryManager = inv;
        this.productionManager = prod;

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
        loadAllTasks();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer(1000, e -> {
            int selectedRow = tasksTable.getSelectedRow();

            int filterType = filterCombo.getSelectedIndex();
            if (filterType == 0) {
                loadAllTasks();
            } else {
                handleFilter();
            }

            if (selectedRow >= 0 && selectedRow < tasksTable.getRowCount()) {
                tasksTable.setRowSelectionInterval(selectedRow, selectedRow);
            }
        });
        refreshTimer.start();
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

        mainContent.add(createFilterPanel(), BorderLayout.NORTH);
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

        JLabel titleLabel = new JLabel("Task Management");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Manage production tasks and assignments");
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

    private JPanel createFilterPanel() {
        ModernPanel panel = new ModernPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, DesignConstants.SPACING_MD, DesignConstants.SPACING_MD));

        JLabel filterLabel = new JLabel("Filter by Status:");
        filterLabel.setFont(DesignConstants.FONT_BODY_BOLD);

        String[] filters = {"All Tasks", "In Progress", "Completed", "Pending", "Canceled"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(DesignConstants.FONT_BODY);
        filterCombo.setPreferredSize(new Dimension(150, DesignConstants.INPUT_HEIGHT));
        filterCombo.addActionListener(e -> handleFilter());

        panel.add(filterLabel);
        panel.add(filterCombo);

        return panel;
    }

    private JPanel createTablePanel() {
        ModernPanel panel = new ModernPanel(new BorderLayout());

        String[] columns = {"Task ID", "Product", "Customer", "Quantity", "Completed", "Progress %", "Status", "Assigned Line", "Delivery Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tasksTable = new JTable(tableModel);
        tasksTable.setFont(DesignConstants.FONT_BODY);
        tasksTable.setRowHeight(40);
        tasksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tasksTable.getTableHeader().setFont(DesignConstants.FONT_BODY_BOLD);
        tasksTable.getTableHeader().setBackground(DesignConstants.SURFACE_DARK);
        tasksTable.setGridColor(DesignConstants.BORDER_LIGHT);

        tasksTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        tasksTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        tasksTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        tasksTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        tasksTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        tasksTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        tasksTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        tasksTable.getColumnModel().getColumn(7).setPreferredWidth(150);
        tasksTable.getColumnModel().getColumn(8).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(tasksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.SPACING_MD, 0));
        panel.setBackground(DesignConstants.BACKGROUND);

        addTaskBtn = new ModernButton("Add Task");
        addTaskBtn.setPreferredSize(new Dimension(120, DesignConstants.BUTTON_HEIGHT));
        addTaskBtn.setSuccess();
        addTaskBtn.addActionListener(e -> showAddTaskDialog());

        viewDetailsBtn = new ModernButton("View Details");
        viewDetailsBtn.setPreferredSize(new Dimension(130, DesignConstants.BUTTON_HEIGHT));
        viewDetailsBtn.addActionListener(e -> showTaskDetails());

        cancelTaskBtn = new ModernButton("Cancel Task");
        cancelTaskBtn.setPreferredSize(new Dimension(120, DesignConstants.BUTTON_HEIGHT));
        cancelTaskBtn.setDanger();
        cancelTaskBtn.addActionListener(e -> handleCancelTask());

        refreshBtn = new ModernButton("Refresh", false);
        refreshBtn.setPreferredSize(new Dimension(100, DesignConstants.BUTTON_HEIGHT));
        refreshBtn.addActionListener(e -> loadAllTasks());

        panel.add(addTaskBtn);
        panel.add(viewDetailsBtn);
        panel.add(cancelTaskBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private void loadAllTasks() {
        tableModel.setRowCount(0);

        for (Task task : inventoryManager.tasks.values()) {
            addTaskToTable(task);
        }
    }

    private void addTaskToTable(Task task) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String lineName = task.getAssignedLine() != null ? task.getAssignedLine().getName() : "Not Assigned";
        String deliveryDate = task.getDeliveryDate() != null ? task.getDeliveryDate().format(formatter) : "N/A";

        Object[] row = {
                task.getId(),
                task.getProduct().getName(),
                task.getCustomerName(),
                task.getQuantityRequired(),
                task.getQuantitycompleted(),
                String.format("%.1f%%", task.getCompletPercent()),
                task.getStatus().toString(),
                lineName,
                deliveryDate
        };
        tableModel.addRow(row);
    }

    private void handleFilter() {
        tableModel.setRowCount(0);

        int filterType = filterCombo.getSelectedIndex();
        List<Task> results;

        switch (filterType) {
            case 0:
                loadAllTasks();
                return;
            case 1:
                results = productionManager.inProgressTask();
                break;
            case 2:
                results = productionManager.completedTask();
                break;
            case 3:
                results = inventoryManager.tasks.values().stream()
                        .filter(t -> t.getStatus() == TaskStatus.PENDING)
                        .toList();
                break;
            case 4:
                results = inventoryManager.tasks.values().stream()
                        .filter(t -> t.getStatus() == TaskStatus.CANCELED)
                        .toList();
                break;
            default:
                loadAllTasks();
                return;
        }

        for (Task task : results) {
            addTaskToTable(task);
        }
    }

    private void showAddTaskDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Task", true);
        dialog.setSize(600, 600);
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

        JLabel productLabel = new JLabel("Select Product:");
        productLabel.setFont(DesignConstants.FONT_BODY_BOLD);
        productLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> productCombo = new JComboBox<>();
        productCombo.setFont(DesignConstants.FONT_BODY);
        productCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        productCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Product product : inventoryManager.products.values()) {
            productCombo.addItem(product.getId() + " - " + product.getName());
        }

        JLabel lineLabel = new JLabel("Select Production Line:");
        lineLabel.setFont(DesignConstants.FONT_BODY_BOLD);
        lineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> lineCombo = new JComboBox<>();
        lineCombo.setFont(DesignConstants.FONT_BODY);
        lineCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        lineCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (ProductLine line : inventoryManager.productsLines.values()) {
            lineCombo.addItem(line.getId() + " - " + line.getName());
        }

        ModernTextField customerField = new ModernTextField(20, "Customer Name");
        ModernTextField quantityField = new ModernTextField(20, "Quantity Required");
        ModernTextField daysField = new ModernTextField(20, "Days Until Delivery");

        customerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        daysField.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));

        panel.add(productLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        panel.add(productCombo);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));

        panel.add(lineLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        panel.add(lineCombo);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));

        panel.add(createDialogField("Customer Name:", customerField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Quantity:", quantityField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Days Until Delivery:", daysField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        ModernButton createButton = new ModernButton("Create Task");
        createButton.setSuccess();
        createButton.addActionListener(e -> {
            try {
                
                String customer = customerField.getText().trim();
                String quantityText = quantityField.getText().trim();
                String daysText = daysField.getText().trim();

                
                if (customer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Customer name cannot be empty!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                
                if (quantityText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter quantity!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Quantity must be a valid number!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Quantity must be greater than 0!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (quantity > 10000) {
                    JOptionPane.showMessageDialog(dialog,
                            "Quantity seems unreasonably high. Please verify.",
                            "Validation Warning",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                
                if (daysText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter delivery days!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int days;
                try {
                    days = Integer.parseInt(daysText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Days must be a valid number!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (days <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Delivery days must be greater than 0!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (days > 365) {
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                            "Delivery is more than 1 year away. Continue?",
                            "Confirm Long Delivery Time",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                
                String productSelection = (String) productCombo.getSelectedItem();
                String lineSelection = (String) lineCombo.getSelectedItem();

                if (productSelection == null || lineSelection == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please select both product and production line!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int productId = Integer.parseInt(productSelection.split(" - ")[0]);
                Product product = inventoryManager.getProductById(productId);

                int lineId = Integer.parseInt(lineSelection.split(" - ")[0]);

                
                int newTaskId = inventoryManager.tasks.size() + 1000;
                LocalDateTime deliveryDate = LocalDateTime.now().plusDays(days);
                Task newTask = new Task(newTaskId, product, quantity, customer, deliveryDate);

                inventoryManager.addTask(newTask);
                productionManager.addTaskToLine(lineId, newTaskId);

                JOptionPane.showMessageDialog(dialog,
                        "Task created successfully!\nTask ID: " + newTaskId,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadAllTasks();

            } catch (MyExceptions ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Unexpected error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel", false);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void handleCancelTask() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task to cancel", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0);
        Task task = inventoryManager.getTaskById(taskId);

        if (task.getStatus() == TaskStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this, "Cannot cancel a completed task", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel Task " + taskId + "?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            task.setStatus(TaskStatus.CANCELED);
            JOptionPane.showMessageDialog(this, "Task canceled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllTasks();
        }
    }

    private void showTaskDetails() {
        int selectedRow = tasksTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a task", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0);
        Task task = inventoryManager.getTaskById(taskId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        StringBuilder details = new StringBuilder();
        details.append("Task Details\n");
        details.append("═══════════════════════════════════════\n\n");
        details.append("Task ID: ").append(task.getId()).append("\n");
        details.append("Product: ").append(task.getProduct().getName()).append("\n");
        details.append("Customer: ").append(task.getCustomerName()).append("\n");
        details.append("Quantity Required: ").append(task.getQuantityRequired()).append("\n");
        details.append("Quantity Completed: ").append(task.getQuantitycompleted()).append("\n");
        details.append("Progress: ").append(String.format("%.1f%%", task.getCompletPercent())).append("\n");
        details.append("Status: ").append(task.getStatus()).append("\n");
        details.append("Assigned Line: ").append(task.getAssignedLine() != null ? task.getAssignedLine().getName() : "Not Assigned").append("\n");
        details.append("Start Date: ").append(task.getStartDate().format(formatter)).append("\n");
        details.append("Delivery Date: ").append(task.getDeliveryDate() != null ? task.getDeliveryDate().format(formatter) : "N/A").append("\n");

        JOptionPane.showMessageDialog(this, details.toString(), "Task Details", JOptionPane.INFORMATION_MESSAGE);
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