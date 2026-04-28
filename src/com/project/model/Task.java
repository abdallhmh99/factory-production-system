package com.project.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Serializable  {

    private static final long serialVersionUID = 1L;

    private int id;
    private Product product;
    private int quantityRequired;
    private int quantitycompleted;
    private String customerName;
    private LocalDateTime startDate;
    private LocalDateTime deliveryDate;
    private TaskStatus status;
    private ProductLine assignedLine;
    private double completPercent; // مثلا : 0.0 / 1.0


    public Task(int id, Product product, int quantityRequired, String customerName, LocalDateTime deliveryDate) {
        this.id = id;
        this.product = product;
        this.quantityRequired = quantityRequired;
        this.customerName = customerName;
        this.status = TaskStatus.PENDING;
        this.completPercent = 0.0;
        this.startDate = LocalDateTime.now();
        this.deliveryDate = deliveryDate;
        this.quantitycompleted = 0;
    }


    //بدون تاريخ تسليم
    public Task(int id, Product product, int quantityRequired, String customerName) {
        this(id, product, quantityRequired, customerName, LocalDateTime.now().plusDays(7));
    }

    //----------------------------------Getters and Setters----------------------------------

    public String getCustomerName() { return customerName; }
    public int getId() { return id; }
    public Product getProduct() { return product; }
    public int getQuantityRequired() { return quantityRequired; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public ProductLine getAssignedLine() { return assignedLine; }
    public void setAssignedLine(ProductLine assignedLine) { this.assignedLine = assignedLine; }
    public double getCompletPercent() { return completPercent; }
    public void setCompletPercent(double completPercent) { this.completPercent = completPercent; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getDeliveryDate() { return deliveryDate; }
    public void setQuantityRequired(int quantityRequired) {this.quantityRequired = quantityRequired;}
    public synchronized void setQuantitycompleted(int quantitycompleted) {this.quantitycompleted = quantitycompleted;}
    public synchronized  int getQuantitycompleted() {return quantitycompleted;}
    public void setDeliveryDate(LocalDateTime deliveryDate) {this.deliveryDate = deliveryDate;}

    @Override
    public String toString() {

        String lineName = (assignedLine != null) ? assignedLine.getName() : "None";

        return String.format("ID:%d, Customer:%s, Product:%s, Qty:%d, Status:%s, Line:%s, Progress:%.1f%%",
                id, customerName, product.getName(), quantityRequired, status, lineName, completPercent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}