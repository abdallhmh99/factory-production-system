package com.project.ui.supervisor;

import com.project.MyExceptions;
import com.project.controller.FileManager;
import com.project.controller.InventoryManager;
import com.project.model.Item;
import com.project.ui.DesignConstants;
import com.project.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class InventoryManagementPanel extends JPanel {

    private InventoryManager inventoryManager;
    private FileManager fileManager;

    private JTable itemsTable;
    private DefaultTableModel tableModel;

    private ModernTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JComboBox<String> filterCombo;
    private ModernButton searchBtn;
    private ModernButton clearFilterBtn;
    private ModernButton addBtn;
    private ModernButton editBtn;
    private ModernButton deleteBtn;
    private ModernButton refreshBtn;
    private ModernButton saveBtn;
    private Timer refreshTimer;

    
    private FilterState currentFilter = new FilterState();

    private static class FilterState {
        String searchTerm = "";
        int searchType = -1; 
        int filterType = 0;  
    }

    public InventoryManagementPanel(InventoryManager inv, FileManager fm) {
        this.inventoryManager = inv;
        this.fileManager = fm;

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
        loadAllItems();
        startAutoRefresh();
    }

    
    private void startAutoRefresh() {
        refreshTimer = new Timer(5000, e -> {
            int selectedRow = itemsTable.getSelectedRow();

            
            reapplyCurrentFilter();

            if (selectedRow >= 0 && selectedRow < itemsTable.getRowCount()) {
                itemsTable.setRowSelectionInterval(selectedRow, selectedRow);
            }
        });
        refreshTimer.start();
    }

    
    private void reapplyCurrentFilter() {
        if (!currentFilter.searchTerm.isEmpty() && currentFilter.searchType >= 0) {
            
            performSearch(currentFilter.searchTerm, currentFilter.searchType);
        } else if (currentFilter.filterType > 0) {
            
            performFilter(currentFilter.filterType);
        } else {
            
            loadAllItems();
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

        mainContent.add(createSearchPanel(), BorderLayout.NORTH);
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

        JLabel titleLabel = new JLabel("Inventory Management");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Manage raw materials and inventory items");
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

    private JPanel createSearchPanel() {
        ModernPanel panel = new ModernPanel();
        panel.setLayout(new BorderLayout(DesignConstants.SPACING_MD, DesignConstants.SPACING_MD));

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, DesignConstants.SPACING_MD, 0));
        searchControls.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(DesignConstants.FONT_BODY_BOLD);

        searchField = new ModernTextField(20, "Enter search term...");

        String[] searchTypes = {"By Name", "By Category"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        searchTypeCombo.setFont(DesignConstants.FONT_BODY);
        searchTypeCombo.setPreferredSize(new Dimension(150, DesignConstants.INPUT_HEIGHT));

        searchBtn = new ModernButton("Search");
        searchBtn.setPreferredSize(new Dimension(100, DesignConstants.INPUT_HEIGHT));
        searchBtn.addActionListener(e -> handleSearch());

        searchControls.add(searchLabel);
        searchControls.add(searchField);
        searchControls.add(searchTypeCombo);
        searchControls.add(searchBtn);

        JPanel filterControls = new JPanel(new FlowLayout(FlowLayout.LEFT, DesignConstants.SPACING_MD, 0));
        filterControls.setOpaque(false);

        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(DesignConstants.FONT_BODY_BOLD);

        String[] filters = {"All Items", "Available", "Out of Stock", "Below Minimum"};
        filterCombo = new JComboBox<>(filters);
        filterCombo.setFont(DesignConstants.FONT_BODY);
        filterCombo.setPreferredSize(new Dimension(150, DesignConstants.INPUT_HEIGHT));
        filterCombo.addActionListener(e -> handleFilter());

        clearFilterBtn = new ModernButton("Clear", false);
        clearFilterBtn.setPreferredSize(new Dimension(100, DesignConstants.INPUT_HEIGHT));
        clearFilterBtn.addActionListener(e -> {
            
            currentFilter = new FilterState();
            filterCombo.setSelectedIndex(0);
            searchField.setText("");
            loadAllItems();
        });

        filterControls.add(filterLabel);
        filterControls.add(filterCombo);
        filterControls.add(clearFilterBtn);

        panel.add(searchControls, BorderLayout.NORTH);
        panel.add(filterControls, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTablePanel() {
        ModernPanel panel = new ModernPanel(new BorderLayout());

        String[] columns = {"ID", "Name", "Category", "Price", "Quantity", "Available", "Min Limit", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        itemsTable = new JTable(tableModel);
        itemsTable.setFont(DesignConstants.FONT_BODY);
        itemsTable.setRowHeight(35);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.getTableHeader().setFont(DesignConstants.FONT_BODY_BOLD);
        itemsTable.getTableHeader().setBackground(DesignConstants.SURFACE_DARK);
        itemsTable.setGridColor(DesignConstants.BORDER_LIGHT);

        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.SPACING_MD, 0));
        panel.setBackground(DesignConstants.BACKGROUND);

        addBtn = new ModernButton("Add Item");
        addBtn.setPreferredSize(new Dimension(120, DesignConstants.BUTTON_HEIGHT));
        addBtn.setSuccess();
        addBtn.addActionListener(e -> showAddItemDialog());

        editBtn = new ModernButton("Edit Item");
        editBtn.setPreferredSize(new Dimension(120, DesignConstants.BUTTON_HEIGHT));
        editBtn.addActionListener(e -> showEditItemDialog());

        deleteBtn = new ModernButton("Delete Item");
        deleteBtn.setPreferredSize(new Dimension(120, DesignConstants.BUTTON_HEIGHT));
        deleteBtn.setDanger();
        deleteBtn.addActionListener(e -> handleDeleteItem());

        refreshBtn = new ModernButton("Refresh", false);
        refreshBtn.setPreferredSize(new Dimension(100, DesignConstants.BUTTON_HEIGHT));
        refreshBtn.addActionListener(e -> reapplyCurrentFilter());

        saveBtn = new ModernButton("Save to File");
        saveBtn.setPreferredSize(new Dimension(120, DesignConstants.BUTTON_HEIGHT));
        saveBtn.setSuccess();
        saveBtn.addActionListener(e -> {
            fileManager.saveInventory();
            JOptionPane.showMessageDialog(this, "Inventory saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(refreshBtn);
        panel.add(saveBtn);

        return panel;
    }

    private void loadAllItems() {
        tableModel.setRowCount(0);
        for (Item item : inventoryManager.items.values()) {
            addItemToTable(item);
        }
    }

    private void addItemToTable(Item item) {
        String status;
        if (item.getQuantity() == 0) {
            status = "Out of Stock";
        } else if (item.getQuantity() < item.getMinLimit()) {
            status = "Low Stock";
        } else {
            status = "Available";
        }

        Object[] row = {
                item.getId(),
                item.getName(),
                item.getCategory(),
                String.format("%.2f", item.getPrice()),
                item.getQuantity(),
                item.getAvailableQuantity(),
                item.getMinLimit(),
                status
        };
        tableModel.addRow(row);
    }

    
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int searchType = searchTypeCombo.getSelectedIndex();

        
        currentFilter.searchTerm = searchTerm;
        currentFilter.searchType = searchType;
        currentFilter.filterType = 0;

        performSearch(searchTerm, searchType);
    }

    
    private void performSearch(String searchTerm, int searchType) {
        tableModel.setRowCount(0);

        List<Item> results;
        if (searchType == 0) {
            results = inventoryManager.searchItemByName(searchTerm);
        } else {
            results = inventoryManager.searchItemBycategory(searchTerm);
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items found", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Item item : results) {
                addItemToTable(item);
            }
        }
    }

    
    private void handleFilter() {
        int filterType = filterCombo.getSelectedIndex();

        
        currentFilter.filterType = filterType;
        currentFilter.searchTerm = "";
        currentFilter.searchType = -1;

        performFilter(filterType);
    }

    
    private void performFilter(int filterType) {
        tableModel.setRowCount(0);

        if (filterType == 0) {
            loadAllItems();
            return;
        }

        List<Item> results;
        switch (filterType) {
            case 1: 
                results = inventoryManager.searchItemByquantity(1);
                break;
            case 2: 
                results = inventoryManager.searchItemByquantity(0);
                break;
            case 3: 
                results = inventoryManager.searchItemByminquantity();
                break;
            default:
                loadAllItems();
                return;
        }

        for (Item item : results) {
            addItemToTable(item);
        }
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Item", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG));
        panel.setBackground(DesignConstants.SURFACE);

        ModernTextField idField = new ModernTextField(20, "Item ID");
        ModernTextField nameField = new ModernTextField(20, "Item Name");
        ModernTextField categoryField = new ModernTextField(20, "Category");
        ModernTextField priceField = new ModernTextField(20, "Price");
        ModernTextField quantityField = new ModernTextField(20, "Quantity");
        ModernTextField minLimitField = new ModernTextField(20, "Minimum Limit");

        panel.add(createDialogField("ID:", idField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Name:", nameField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Category:", categoryField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Price:", priceField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Quantity:", quantityField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Min Limit:", minLimitField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        ModernButton saveButton = new ModernButton("Add Item");
        saveButton.setSuccess();
        saveButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                int minLimit = Integer.parseInt(minLimitField.getText().trim());

                
                if (price < 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Price cannot be negative!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (quantity < 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Quantity cannot be negative!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (minLimit < 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Minimum limit cannot be negative!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (name.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Name and category cannot be empty!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Item newItem = new Item(id, name, category, price, quantity, minLimit);
                inventoryManager.addItem(newItem);

                JOptionPane.showMessageDialog(dialog,
                        "Item added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                reapplyCurrentFilter();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter valid numbers for ID, Price, Quantity, and Min Limit",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (MyExceptions ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel", false);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showEditItemDialog() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        Item item = inventoryManager.getItemById(itemId);

        if (item == null) {
            JOptionPane.showMessageDialog(this, "Item not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Item", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(DesignConstants.SPACING_LG, DesignConstants.SPACING_LG, DesignConstants.SPACING_LG, DesignConstants.SPACING_LG));
        panel.setBackground(DesignConstants.SURFACE);

        ModernTextField nameField = new ModernTextField(20);
        nameField.setText(item.getName());

        ModernTextField categoryField = new ModernTextField(20);
        categoryField.setText(item.getCategory());

        ModernTextField priceField = new ModernTextField(20);
        priceField.setText(String.valueOf(item.getPrice()));

        ModernTextField quantityField = new ModernTextField(20);
        quantityField.setText(String.valueOf(item.getQuantity()));

        ModernTextField minLimitField = new ModernTextField(20);
        minLimitField.setText(String.valueOf(item.getMinLimit()));

        panel.add(createDialogField("Name:", nameField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Category:", categoryField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Price:", priceField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Quantity:", quantityField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Min Limit:", minLimitField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        ModernButton saveButton = new ModernButton("Save Changes");
        saveButton.setSuccess();
        saveButton.addActionListener(e -> {
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                int minLimit = Integer.parseInt(minLimitField.getText().trim());

                
                if (price < 0 || quantity < 0 || minLimit < 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Values cannot be negative!",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                item.setName(nameField.getText().trim());
                item.setCategory(categoryField.getText().trim());
                item.setPrice(price);
                item.setQuantity(quantity);
                item.setMinLimit(minLimit);

                JOptionPane.showMessageDialog(dialog, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                reapplyCurrentFilter();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel", false);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void handleDeleteItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        String itemName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete item: " + itemName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            inventoryManager.removeItem(itemId);
            JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            reapplyCurrentFilter();
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

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        panel.add(field);

        return panel;
    }
}