package com.project.controller;

import com.project.MyExceptions;
import com.project.User.User;
import com.project.User.UserManager;
import com.project.model.Item;
import com.project.model.Product;
import com.project.model.ProductLine;
import com.project.model.Task;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class FileManager {
    private static final String DIR_PATH = "inventory";

    private final String itemsPath = DIR_PATH + File.separator + "Items.ser";
    private final String productsPath = DIR_PATH + File.separator + "Product.ser";
    private final String productLinesPath = DIR_PATH + File.separator + "Product_Lines.ser";
    private final String tasksPath = DIR_PATH + File.separator + "Task.ser";

    private final String erorrPath = DIR_PATH + "error.txt";

    InventoryManager inv = InventoryManager.getInstance();

    public FileManager() {
        createDirectoryIfNotExists();

    }

    private void createDirectoryIfNotExists() {
        File directory = new File(DIR_PATH);

        if (!directory.exists()) {
            boolean created = directory.mkdir();
            if (created) {
                System.out.println("تم إنشاء مجلد " + DIR_PATH + " بنجاح.");
            } else {
                System.err.println("فشل في إنشاء المجلد (قد يكون موجوداً مسبقاً أو لا توجد صلاحيات).");
            }
        }
    }




    public Object loadUsers () throws MyExceptions {

        File file = new File("Users.ser");
        if (!file.exists()) {
            throw new MyExceptions("file of users not found !");
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Users.ser"))) {
            return ois.readObject();

        }  catch (IOException | ClassNotFoundException e) {
        FileManager.logError("Failed to load file: " + e.getMessage());
        return null;
    }

    }

    public void saveUsers (List users) {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Users.ser"))){
            oos.writeObject(users);

        } catch (FileNotFoundException e) {
            FileManager.logError(e.getMessage());
        } catch (IOException e) {
            FileManager.logError(e.getMessage());
        }
    }


    public void saveInventory (){


        saveMap(inv.items,itemsPath);
        saveMap(inv.products,productsPath);
        saveMap(inv.productsLines,productLinesPath);
        saveMap(inv.tasks,tasksPath);
    }

    private void saveMap(Map<?, ?> map, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {

            oos.writeObject(map);
        } catch (IOException e) {
            System.err.println("Error saving " + filePath + ": " + e.getMessage());
            logError("Critical: Failed to save data to " + filePath + " - " + e.getMessage());
        }
    }

    public void loadInventory() {
        InventoryManager inv = InventoryManager.getInstance();

        Object itemLoad = loadMap(itemsPath);
        if (itemLoad != null) inv.items = (Map<Integer, Item>) itemLoad;

        if (inv.items != null) {
            for (Item item : inv.items.values()) {
                if (item.getReservedQuantity() > 0) {
                    item.unreserve(item.getReservedQuantity());
                }
            }
            System.out.println(" تم إعادة تعيين الكميات المحجوزة (Data Cleanup).");
        }

        Object productLoad = loadMap(productsPath);
        if (productLoad != null) inv.products = (Map<Integer, Product>) productLoad;

        Object productLinesLoad = loadMap(productLinesPath);
        if (productLinesLoad != null) inv.productsLines = (Map<Integer, ProductLine>) productLinesLoad;

        Object taskLoad = loadMap(tasksPath);
        if (taskLoad != null) inv.tasks = (Map<Integer, Task>) taskLoad;


        for (ProductLine line : inv.productsLines.values()) {
            line.getTasks().clear();
        }

        for (Task task : inv.tasks.values()) {

            if (task.getProduct() != null) {
                Product realProduct = inv.getProductById(task.getProduct().getId());
                if (realProduct != null) {

                }
            }

            if (task.getAssignedLine() != null) {
                ProductLine realLine = inv.getProductLineById(task.getAssignedLine().getId());
                if (realLine != null) {
                    task.setAssignedLine(realLine);
                    if (task.getStatus() == com.project.model.TaskStatus.PENDING ||
                            task.getStatus() == com.project.model.TaskStatus.IN_PROGRESS) {
                        realLine.addTask(task);
                    }
                }
            }
        }

        System.out.println(" تم تحميل البيانات وتوحيد المراجع بنجاح.");
        ThreadManager.getInstance().startAllActiveLines();
    }

    private Object loadMap ( String filePath){

        File file = new File(filePath);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {

            return ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            logError("Warning: Failed to load data from " + filePath + " (File might be missing or corrupted).");
            return null;
        }
    }

    public String writeInventory() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fileNameFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String fileName = "inventory_report_" + now.format(fileNameFormat) + ".txt";
        String fullPath = DIR_PATH + File.separator +  fileName;

        DateTimeFormatter headerFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String prettyTime = now.format(headerFormat);

        InventoryManager inv = InventoryManager.getInstance();

        try (PrintWriter printWriter = new PrintWriter(new FileWriter(fullPath))) {


            printWriter.println("==========================================");
            printWriter.println("       PRODUCTION SYSTEM REPORT");
            printWriter.println("       Time: " + prettyTime);
            printWriter.println("==========================================\n");

            writeSection(printWriter, "1 : Items : ", inv.items);
            writeSection(printWriter, "2 : Products : ", inv.products);
            writeSection(printWriter, "3 : Product Lines : ", inv.productsLines);
            writeSection(printWriter, "4 : Tasks : ", inv.tasks);

            System.out.println("تم حفظ التقرير بنجاح.");

            return fullPath;

        }  catch (IOException e) {
        FileManager.logError("Report Generation Failed: " + e.getMessage());
        return null;
    }

    }

    private void writeSection(PrintWriter writer, String header, Map<?, ?> map) {
        writer.println(header);
        writer.println("------------------------------------------------");


        if (map.isEmpty()) {
            writer.println("   (No Data Available)");
        } else {

        int count = 1;
        for (Object object : map.values()) {
            writer.println(count + " - " + object.toString());
            count++;
        }
        writer.println();
    }}


    public static void logError(String message) {
        String errorFile = "error.txt";
        String time = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(errorFile, true))) {
            writer.println("[" + time + "] ERROR: " + message);
        } catch (java.io.IOException e) {
            System.err.println("فشل تسجيل الخطأ: " + e.getMessage());
        }
    }


}
