package com.project.controller;

import com.project.model.*;
import com.project.ui.supervisor.ProductionReportsPanel;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProductionManager {

    InventoryManager inv = InventoryManager.getInstance();

    public boolean changeLineState(int id, LineStatus lineStatus) {
        ProductLine line = inv.getProductLineById(id);

        if (line != null) {
            LineStatus oldStatus = line.getStatus();
            line.setStatus(lineStatus);

            if (lineStatus == LineStatus.ACTIVE && oldStatus != LineStatus.ACTIVE) {
                ThreadManager.getInstance().startLineThread(line);
            }

            return true; //سكرة
        }
        return false;  // الخط غير موجود
    }

    public void addTaskToLine(int idLine, int idTask) {

        ProductLine line = inv.getProductLineById(idLine);
        Task task = inv.getTaskById(idTask);

        if (line != null && task != null) {
            line.addTask(task);

            task.setAssignedLine(line);
            if (task.getStatus() != TaskStatus.COMPLETED && task.getStatus() != TaskStatus.CANCELED) {
                task.setStatus(TaskStatus.PENDING);
            }
        } else {
            String msg = "Error: Failed to assign task " + idTask + " to line " + idLine;
            System.out.println(msg);
            FileManager.logError(msg);
        }
    }



    public Queue<Task> showTaskProductLine(int idProductLine){
        return inv.getProductLineById(idProductLine).getTasks();
    }

    public List<Task> showTasksForProduct(int idProduct) {
        Product product = inv.getProductById(idProduct);


        if (product == null) {

            System.out.println("Product not found: " + idProduct);
            return new ArrayList<>();
        }

        List<Task> tasksForProduct = new LinkedList<>();
        for (Task task : inv.tasks.values()) {

            if (task.getProduct().getId() == idProduct) {
                tasksForProduct.add(task);
            }
        }
        return tasksForProduct;
    }

    public List<Task> completedTask() {
        List<Task> completed = new ArrayList<>();

        for (Task task : inv.tasks.values()) {
            if (task.getStatus() == TaskStatus.COMPLETED) {
                completed.add(task);
            }
        }
        return completed;
    }

    public List<Task> inProgressTask() {
        List<Task> inProgress = new ArrayList<>();

        for (Task task : inv.tasks.values()) {
            if (task.getStatus() == TaskStatus.IN_PROGRESS){
                inProgress.add(task);
            }
        }
        return inProgress;
    }



    public Map<ProductLine, Integer> getLinesThatProduced (int idProduct){
        Map <ProductLine,Integer> a = new HashMap<>();

        for (ProductLine productLine : inv.productsLines.values()){
            int taskCount = 0;
            for (Task task : productLine.getCompletedTasks()){
                if (task.getProduct().equals(inv.getProductById(idProduct))){
                    taskCount++;
                }
            }
            if (taskCount > 0) {
                a.put(productLine, taskCount);
            }
        }
        return a;
    }

    public List<Product> productForLine(int idProductLine) {
        return inv.getProductLineById(idProductLine).getProducts();
    }

    public Map<ProductLine, List<Product>> productForAllLine() {
        Map<ProductLine, List<Product>> mapAllProducts = new ConcurrentHashMap<>();

        for (ProductLine productLine : inv.productsLines.values()) {
            mapAllProducts.put(productLine, productLine.getProducts());
        }
        return mapAllProducts;
    }

    public Map<Product, Integer> mostProduct(LocalDateTime start, LocalDateTime end) {
        Map<Product, Integer> productCounts = new ConcurrentHashMap<>();

        for (Task task : inv.tasks.values()) {
            LocalDateTime taskStart = task.getStartDate();
            LocalDateTime taskDelivery = task.getDeliveryDate();


            boolean isInPeriod = (taskStart.isAfter(start) && taskStart.isBefore(end)) ||
                    (taskDelivery != null && taskDelivery.isAfter(start) && taskDelivery.isBefore(end)) ||
                    (taskStart.isBefore(end) && (taskDelivery == null || taskDelivery.isAfter(start)));

            if (isInPeriod) {
                productCounts.compute(task.getProduct(),
                        (k, v) -> (v == null ? 0 : v) + 1);
            }
        }

        return productCounts;
    }

















































    public void etLinesThatProduced(){
        getLinesThatProduced(1);
        productForLine(1);
    };

}













