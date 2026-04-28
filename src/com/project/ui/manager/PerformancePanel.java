package com.project.ui.manager;

import com.project.controller.InventoryManager;
import com.project.controller.ProductionManager;
import com.project.model.ProductLine;
import com.project.ui.DesignConstants;
import com.project.ui.components.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class PerformancePanel extends JPanel {

    private InventoryManager inventoryManager;
    private ProductionManager productionManager;

    private JTable performanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> ratingComboBox;
    private JTextArea notesArea;
    private ModernButton saveButton;
    private ModernButton refreshButton;
    private JLabel selectedLineLabel;

    private Timer refreshTimer;

    public PerformancePanel(InventoryManager inv, ProductionManager prod) {
        this.inventoryManager = inv;
        this.productionManager = prod;

        setBackground(DesignConstants.BACKGROUND);
        setLayout(new BorderLayout());

        initializeUI();
        loadPerformanceData();
        startAutoRefresh(); 
    }

    private boolean isEditing = false;

    private void startAutoRefresh() {
        refreshTimer = new Timer(3000, e -> {
            if (!isEditing) {
                loadPerformanceData();
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

        
        contentPanel.add(createEvaluationPanel(), BorderLayout.EAST);

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

        JLabel titleLabel = new JLabel("Performance & Evaluation");
        titleLabel.setFont(DesignConstants.FONT_H1);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("View and evaluate production line performance");
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

        JLabel tableTitle = new JLabel("Production Line Performance");
        tableTitle.setFont(DesignConstants.FONT_H3);
        tableTitle.setForeground(DesignConstants.TEXT_PRIMARY);

        refreshButton = new ModernButton("Refresh", false);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> loadPerformanceData());

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableHeader.add(refreshButton, BorderLayout.EAST);

        
        String[] columns = {"ID", "Name", "Status", "Completed", "Products Made", "Rating", "Progress %"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        performanceTable = new JTable(tableModel);
        performanceTable.setFont(DesignConstants.FONT_BODY);
        performanceTable.setRowHeight(40);
        performanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        performanceTable.getTableHeader().setFont(DesignConstants.FONT_BODY_BOLD);
        performanceTable.getTableHeader().setBackground(DesignConstants.SURFACE_DARK);
        performanceTable.setGridColor(DesignConstants.BORDER_LIGHT);

        
        performanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedLineEvaluation();
            }
        });

        JScrollPane scrollPane = new JScrollPane(performanceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(DesignConstants.BORDER_LIGHT));

        panel.add(tableHeader, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEvaluationPanel() {
        ModernPanel panel = new ModernPanel();
        panel.setPreferredSize(new Dimension(380, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        
        JLabel titleLabel = new JLabel("Evaluation");
        titleLabel.setFont(DesignConstants.FONT_H3);
        titleLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        
        selectedLineLabel = new JLabel("No line selected");
        selectedLineLabel.setFont(DesignConstants.FONT_BODY);
        selectedLineLabel.setForeground(DesignConstants.TEXT_SECONDARY);
        selectedLineLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        
        JPanel ratingGroup = new JPanel();
        ratingGroup.setOpaque(false);
        ratingGroup.setLayout(new BoxLayout(ratingGroup, BoxLayout.Y_AXIS));
        ratingGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ratingLabel = new JLabel("Performance Rating");
        ratingLabel.setFont(DesignConstants.FONT_BODY_BOLD);
        ratingLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] ratings = {"Excellent", "Very Good", "Good", "Fair", "Poor"};
        ratingComboBox = new JComboBox<>(ratings);
        ratingComboBox.setFont(DesignConstants.FONT_BODY);
        ratingComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.INPUT_HEIGHT));
        ratingComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        ratingGroup.add(ratingLabel);
        ratingGroup.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        ratingGroup.add(ratingComboBox);

        
        JPanel notesGroup = new JPanel();
        notesGroup.setOpaque(false);
        notesGroup.setLayout(new BoxLayout(notesGroup, BoxLayout.Y_AXIS));
        notesGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel notesLabel = new JLabel("Manager Notes");
        notesLabel.setFont(DesignConstants.FONT_BODY_BOLD);
        notesLabel.setForeground(DesignConstants.TEXT_PRIMARY);
        notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        notesArea = new JTextArea(6, 30);
        notesArea.setFont(DesignConstants.FONT_BODY);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DesignConstants.BORDER_MEDIUM),
                BorderFactory.createEmptyBorder(
                        DesignConstants.SPACING_SM,
                        DesignConstants.SPACING_SM,
                        DesignConstants.SPACING_SM,
                        DesignConstants.SPACING_SM
                )
        ));

        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        notesGroup.add(notesLabel);
        notesGroup.add(Box.createVerticalStrut(DesignConstants.SPACING_SM));
        notesGroup.add(notesScroll);

        
        saveButton = new ModernButton("Save Evaluation");
        saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, DesignConstants.BUTTON_HEIGHT));
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setEnabled(false);
        saveButton.setSuccess();
        saveButton.addActionListener(e -> handleSaveEvaluation());

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_MD));
        panel.add(selectedLineLabel);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_XL));
        panel.add(ratingGroup);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));
        panel.add(notesGroup);
        panel.add(Box.createVerticalStrut(DesignConstants.SPACING_LG));
        panel.add(saveButton);

        return panel;
    }

    private void loadPerformanceData() {
        tableModel.setRowCount(0);

        for (ProductLine line : inventoryManager.productsLines.values()) {
            int completedTasks = line.getCompletedTasks().size();
            int productsMade = line.getProducts().size();
            double progress = line.getCompletPercentForCurrentTask();

            Object[] row = {
                    line.getId(),
                    line.getName(),
                    line.getStatus().toString(),
                    completedTasks,
                    productsMade,
                    line.getManagerRating(),
                    String.format("%.1f%%", progress)
            };
            tableModel.addRow(row);
        }
    }

    private void updateSelectedLineEvaluation() {
        int selectedRow = performanceTable.getSelectedRow();

        if (selectedRow >= 0) {
            int lineId = (int) tableModel.getValueAt(selectedRow, 0);
            ProductLine line = inventoryManager.getProductLineById(lineId);

            if (selectedRow >= 0) {
                isEditing = true;
                selectedLineLabel.setText("Selected: " + line.getName() + " (ID: " + lineId + ")");

                
                String currentRating = line.getManagerRating();
                String currentNotes = line.getManagerNotes();

                
                switch (currentRating) {
                    case "Excellent" -> ratingComboBox.setSelectedIndex(0);
                    case "Very Good" -> ratingComboBox.setSelectedIndex(1);
                    case "Good" -> ratingComboBox.setSelectedIndex(2);
                    case "Fair" -> ratingComboBox.setSelectedIndex(3);
                    case "Poor" -> ratingComboBox.setSelectedIndex(4);
                    default -> ratingComboBox.setSelectedIndex(2);
                }

                
                notesArea.setText(currentNotes.equals("new") ? "" : currentNotes);

                saveButton.setEnabled(true);
            }
        } else {
            selectedLineLabel.setText("No line selected");
            saveButton.setEnabled(false);
            notesArea.setText("");
        }
    }

    private void handleSaveEvaluation() {
        int selectedRow = performanceTable.getSelectedRow();
        if (selectedRow < 0) return;

        int lineId = (int) tableModel.getValueAt(selectedRow, 0);
        String rating = (String) ratingComboBox.getSelectedItem();
        String notes = notesArea.getText().trim();

        
        inventoryManager.updateLineEvaluation(lineId, rating, notes);

        JOptionPane.showMessageDialog(this,
                "Evaluation saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        isEditing = false;
        loadPerformanceData();
    }
}