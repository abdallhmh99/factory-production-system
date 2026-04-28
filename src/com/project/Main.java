package com.project;

import com.project.controller.FileManager;
import com.project.controller.InitialDataLoader;
import com.project.ui.LoginScreen;
import javax.swing.*;
import java.io.File;


public class Main {

    private static final boolean DEMO_MODE = true;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // في وضع العرض: حذف البيانات القديمة لضمان بداية جديدة كل مرة
        if (DEMO_MODE) {
            clearSavedData();
        }

        FileManager fileManager = new FileManager();
        fileManager.loadInventory();
        startAutoSave(fileManager);

        if (DEMO_MODE && InitialDataLoader.isInventoryEmpty()) {

            System.out.println("      DEMO MODE: Loading Fresh Sample Data    ");


            InitialDataLoader loader = new InitialDataLoader();
            loader.loadInitialData();


            fileManager.saveInventory();
            System.out.println(" Demo data saved to files.\n");
        } else if (!InitialDataLoader.isInventoryEmpty()) {
            System.out.println("\n Loaded existing data from files.");
            System.out.println("   Items: " + com.project.controller.InventoryManager.getInstance().items.size());
            System.out.println("   Products: " + com.project.controller.InventoryManager.getInstance().products.size());
            System.out.println("   Production Lines: " + com.project.controller.InventoryManager.getInstance().productsLines.size());
            System.out.println("   Tasks: " + com.project.controller.InventoryManager.getInstance().tasks.size() + "\n");
        } else {
            System.out.println("\n  Starting with empty inventory (DEMO_MODE is disabled).\n");
        }


        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }

    private static void startAutoSave(FileManager fileManager) {
        Thread autoSaveThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep( 3000); // حفظ كل ثانيتين
                    System.out.println("💾 Auto-saving inventory data...");
                    fileManager.saveInventory();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        autoSaveThread.setDaemon(true); //اغلاق تلقائي
        autoSaveThread.start();
    }

    private static void clearSavedData() {
        File dir = new File("inventory");
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".ser")) {
                        f.delete();
                    }
                }
            }
            System.out.println("🧹 Demo data cleared for a fresh start.");
        }
    }
}