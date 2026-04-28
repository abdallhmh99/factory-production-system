package com.project.ui.supervisor;

import com.project.MyExceptions;
import com.project.controller.InventoryManager;
import com.project.model.Item;
import com.project.model.Product;
import com.project.ui.DesignConstants;
import com.project.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;


public class ProductManagementPanel extends JPanel {

    private InventoryManager inventoryManager;

    private JTable productsTable;
    private DefaultTableModel tableModel;

    private ModernTextField searchField;
    private ModernButton searchBtn;
    private ModernButton viewMaterialsBtn;
    private ModernButton refreshBtn;

    private Timer refreshTimer;

    
    private String currentSearchTerm = "";

    public ProductManagementPanel(InventoryManager inv) {
        this.inventoryManager = inv;

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
        loadAllProducts();
        startAutoRefresh();
    }

    
    private void startAutoRefresh() {
        refreshTimer = new Timer(5000, e -> {
            int selectedRow = productsTable.getSelectedRow();

            if (!currentSearchTerm.isEmpty()) {
                performSearch(currentSearchTerm);
            } else {
                loadAllProducts();
            }

            if (selectedRow >= 0 && selectedRow < productsTable.getRowCount()) {
                productsTable.setRowSelectionInterval(selectedRow, selectedRow);
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

        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("View and manage manufactured products");
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
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, DesignConstants.SPACING_MD, DesignConstants.SPACING_MD));

        JLabel searchLabel = new JLabel("Search by Name:");
        searchLabel.setFont(DesignConstants.FONT_BODY_BOLD);

        searchField = new ModernTextField(25, "Enter product name...");

        searchBtn = new ModernButton("Search");
        searchBtn.setPreferredSize(new Dimension(100, DesignConstants.INPUT_HEIGHT));
        searchBtn.addActionListener(e -> handleSearch());

        ModernButton clearBtn = new ModernButton("Clear", false);
        clearBtn.setPreferredSize(new Dimension(100, DesignConstants.INPUT_HEIGHT));
        clearBtn.addActionListener(e -> {
            currentSearchTerm = "";
            searchField.setText("");
            loadAllProducts();
        });

        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchBtn);
        panel.add(clearBtn);

        return panel;
    }

    private JPanel createTablePanel() {
        ModernPanel panel = new ModernPanel(new BorderLayout());

        String[] columns = {"ID", "Name", "Stock Quantity", "Materials Required", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productsTable = new JTable(tableModel);
        productsTable.setFont(DesignConstants.FONT_BODY);
        productsTable.setRowHeight(40);
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsTable.getTableHeader().setFont(DesignConstants.FONT_BODY_BOLD);
        productsTable.getTableHeader().setBackground(DesignConstants.SURFACE_DARK);
        productsTable.setGridColor(DesignConstants.BORDER_LIGHT);

        productsTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        productsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productsTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        productsTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        productsTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(productsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, DesignConstants.SPACING_MD, 0));
        panel.setBackground(DesignConstants.BACKGROUND);

        ModernButton addProductBtn = new ModernButton("Add Product");
        addProductBtn.setPreferredSize(new Dimension(130, DesignConstants.BUTTON_HEIGHT));
        addProductBtn.setSuccess();
        addProductBtn.addActionListener(e -> showAddProductDialog());

        viewMaterialsBtn = new ModernButton("Manage Materials");
        viewMaterialsBtn.setPreferredSize(new Dimension(150, DesignConstants.BUTTON_HEIGHT));
        viewMaterialsBtn.addActionListener(e -> showMaterialsDialog());

        refreshBtn = new ModernButton("Refresh", false);
        refreshBtn.setPreferredSize(new Dimension(100, DesignConstants.BUTTON_HEIGHT));
        refreshBtn.addActionListener(e -> {
            if (!currentSearchTerm.isEmpty()) {
                performSearch(currentSearchTerm);
            } else {
                loadAllProducts();
            }
        });

        panel.add(addProductBtn);
        panel.add(viewMaterialsBtn);
        panel.add(refreshBtn);

        return panel;
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Product", true);
        dialog.setSize(600, 500);
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

        ModernTextField idField = new ModernTextField(20, "Product ID");
        ModernTextField nameField = new ModernTextField(20, "Product Name");
        ModernTextField quantityField = new ModernTextField(20, "Initial Quantity");

        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));

        panel.add(createDialogField("Product ID:", idField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Product Name:", nameField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Initial Quantity:", quantityField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        JLabel infoLabel = new JLabel("<html>Note: You can add materials to this product later<br>by selecting it and clicking 'Manage Materials'</html>");
        infoLabel.setFont(DesignConstants.FONT_SMALL);
        infoLabel.setForeground(DesignConstants.TEXT_SECONDARY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(infoLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        ModernButton createButton = new ModernButton("Create Product");
        createButton.setSuccess();
        createButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter product name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (quantity < 0) {
                    JOptionPane.showMessageDialog(dialog, "Quantity cannot be negative!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Product newProduct = new Product(id, name, quantity);
                inventoryManager.addProduct(newProduct);

                JOptionPane.showMessageDialog(dialog,
                        "Product created successfully!\nYou can now add materials to it.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();

                
                if (!currentSearchTerm.isEmpty()) {
                    performSearch(currentSearchTerm);
                } else {
                    loadAllProducts();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (MyExceptions ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void loadAllProducts() {
        tableModel.setRowCount(0);

        for (Product product : inventoryManager.products.values()) {
            addProductToTable(product);
        }
    }

    private void addProductToTable(Product product) {
        StringBuilder materials = new StringBuilder();
        Map<Item, Integer> productMaterials = product.getMaterials();

        if (productMaterials.isEmpty()) {
            materials.append("No materials defined");
        } else {
            int count = 0;
            for (Map.Entry<Item, Integer> entry : productMaterials.entrySet()) {
                if (count > 0) materials.append(", ");
                materials.append(entry.getKey().getName()).append(" (").append(entry.getValue()).append(")");
                count++;
            }
        }

        String status = product.getQuantity() > 0 ? "In Stock" : "Out of Stock";

        Object[] row = {
                product.getId(),
                product.getName(),
                product.getQuantity(),
                materials.toString(),
                status
        };
        tableModel.addRow(row);
    }

    
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a product name", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentSearchTerm = searchTerm;
        performSearch(searchTerm);
    }

    
    private void performSearch(String searchTerm) {
        tableModel.setRowCount(0);

        java.util.List<Product> results = inventoryManager.searchProductByName(searchTerm);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products found", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Product product : results) {
                addProductToTable(product);
            }
        }
    }

    private void showMaterialsDialog() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        Product product = inventoryManager.getProductById(productId);

        if (product == null) {
            JOptionPane.showMessageDialog(this, "Product not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Product Materials", true);
        dialog.setSize(700, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(DesignConstants.SPACING_LG, DesignConstants.SPACING_LG));
        panel.setBorder(BorderFactory.createEmptyBorder(
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG,
                DesignConstants.SPACING_LG
        ));
        panel.setBackground(DesignConstants.SURFACE);

        JLabel titleLabel = new JLabel("Materials for: " + product.getName());
        titleLabel.setFont(DesignConstants.FONT_H2);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        String[] columns = {"Material", "Quantity Required", "Available Stock", "Status"};
        DefaultTableModel materialsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable materialsTable = new JTable(materialsModel);
        materialsTable.setFont(DesignConstants.FONT_BODY);
        materialsTable.setRowHeight(35);
        materialsTable.getTableHeader().setFont(DesignConstants.FONT_BODY_BOLD);
        materialsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable loadMaterials = () -> {
            materialsModel.setRowCount(0);
            Map<Item, Integer> materials = product.getMaterials();
            for (Map.Entry<Item, Integer> entry : materials.entrySet()) {
                Item item = entry.getKey();
                int required = entry.getValue();
                int available = item.getQuantity();
                String status = available >= required ? "Sufficient" : "Insufficient";

                Object[] row = {item.getName(), required, available, status};
                materialsModel.addRow(row);
            }
        };

        loadMaterials.run();

        JScrollPane scrollPane = new JScrollPane(materialsTable);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, DesignConstants.SPACING_MD, 0));
        actionPanel.setOpaque(false);

        ModernButton addMaterialBtn = new ModernButton("Add Material");
        addMaterialBtn.setSuccess();
        addMaterialBtn.addActionListener(e -> {
            showAddMaterialDialog(product, loadMaterials);
        });

        ModernButton removeMaterialBtn = new ModernButton("Remove Material");
        removeMaterialBtn.setDanger();
        removeMaterialBtn.addActionListener(e -> {
            int selectedMaterialRow = materialsTable.getSelectedRow();
            if (selectedMaterialRow < 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a material to remove", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String materialName = (String) materialsModel.getValueAt(selectedMaterialRow, 0);

            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Remove material: " + materialName + "?",
                    "Confirm Remove",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                Item itemToRemove = null;
                for (Item item : product.getMaterials().keySet()) {
                    if (item.getName().equals(materialName)) {
                        itemToRemove = item;
                        break;
                    }
                }

                if (itemToRemove != null) {
                    product.removeMaterials(itemToRemove);
                    loadMaterials.run();

                    
                    if (!currentSearchTerm.isEmpty()) {
                        performSearch(currentSearchTerm);
                    } else {
                        loadAllProducts();
                    }

                    JOptionPane.showMessageDialog(dialog, "Material removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        actionPanel.add(addMaterialBtn);
        actionPanel.add(removeMaterialBtn);

        ModernButton closeBtn = new ModernButton("Close", false);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(actionPanel, BorderLayout.WEST);

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setOpaque(false);
        closePanel.add(closeBtn);
        bottomPanel.add(closePanel, BorderLayout.EAST);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void showAddMaterialDialog(Product product, Runnable refreshCallback) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Material", true);
        dialog.setSize(500, 300);
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

        JLabel itemLabel = new JLabel("Select Material (Item):");
        itemLabel.setFont(DesignConstants.FONT_BODY_BOLD);
        itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> itemCombo = new JComboBox<>();
        itemCombo.setFont(DesignConstants.FONT_BODY);
        itemCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        itemCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Item item : inventoryManager.items.values()) {
            itemCombo.addItem(item.getId() + " - " + item.getName() + " (Available: " + item.getQuantity() + ")");
        }

        ModernTextField quantityField = new ModernTextField(20, "Quantity needed per unit");
        quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));

        panel.add(itemLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XS));
        panel.add(itemCombo);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(createDialogField("Quantity Required:", quantityField));
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        ModernButton addButton = new ModernButton("Add Material");
        addButton.setSuccess();
        addButton.addActionListener(e -> {
            try {
                String itemSelection = (String) itemCombo.getSelectedItem();
                if (itemSelection == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select an item", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int itemId = Integer.parseInt(itemSelection.split(" - ")[0]);
                Item selectedItem = inventoryManager.getItemById(itemId);

                int quantity = Integer.parseInt(quantityField.getText().trim());

                
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Quantity must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                product.addOrEditMaterial(selectedItem, quantity);

                JOptionPane.showMessageDialog(dialog, "Material added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshCallback.run();

                
                if (!currentSearchTerm.isEmpty()) {
                    performSearch(currentSearchTerm);
                } else {
                    loadAllProducts();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        ModernButton cancelButton = new ModernButton("Cancel", false);
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
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