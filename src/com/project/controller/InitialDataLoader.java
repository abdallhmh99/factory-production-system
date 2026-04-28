package com.project.controller;

import com.project.MyExceptions;
import com.project.model.*;
import java.time.LocalDateTime;


public class InitialDataLoader {

    private InventoryManager inventoryManager;
    private ProductionManager productionManager;

    public InitialDataLoader() {
        this.inventoryManager = InventoryManager.getInstance();
        this.productionManager = new ProductionManager();
    }


    public void loadInitialData() {
        System.out.println("\n========================================");
        System.out.println(" Loading Professional Demo Data...");
        System.out.println("========================================\n");

        try {
            loadItems();
            loadProducts();
            loadProductLines();
            loadTasks();

            System.out.println("\n========================================");
            System.out.println(" Initial Data Loaded Successfully!");
            System.out.println("========================================\n");

            printSummary();

        } catch (Exception e) {
            System.err.println(" Error loading initial data: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void loadItems() throws MyExceptions {
        System.out.println(" Loading Items...");

        // Electronics
        inventoryManager.addItem(new Item(1, "Quantum Processor", "Electronics", 150.0, 500, 50));
        inventoryManager.addItem(new Item(2, "OLED Display Panel", "Electronics", 85.0, 300, 100));
        inventoryManager.addItem(new Item(3, "Memory Chip 16GB", "Electronics", 45.0, 1200, 200));
        inventoryManager.addItem(new Item(4, "Optical Sensor", "Electronics", 12.0, 300, 50));
        inventoryManager.addItem(new Item(5, "Lithium Battery Cell", "Electronics", 22.0, 200, 150));

        // Metals
        inventoryManager.addItem(new Item(6, "Industrial Steel Sheet", "Metals", 120.0, 800, 100));
        inventoryManager.addItem(new Item(7, "Aluminum Extrusion", "Metals", 75.0, 600, 80));
        inventoryManager.addItem(new Item(8, "Copper Wire Reel", "Metals", 35.0, 250, 50));
        inventoryManager.addItem(new Item(9, "Titanium Alloy", "Metals", 300.0, 60, 20));

        // Plastics & Materials
        inventoryManager.addItem(new Item(10, "ABS Plastic Pellets", "Materials", 5.0, 5000, 1000));
        inventoryManager.addItem(new Item(11, "Carbon Fiber Roll", "Materials", 210.0, 200, 10));
        inventoryManager.addItem(new Item(12, "Synthetic Rubber", "Materials", 18.0, 350, 100));

        // Mechanical Parts
        inventoryManager.addItem(new Item(13, "Servo Motor High-Torque", "Mechanical", 95.0, 300, 30));
        inventoryManager.addItem(new Item(14, "Hydraulic Piston", "Mechanical", 130.0, 80, 25));
        inventoryManager.addItem(new Item(15, "Precision Bearings", "Mechanical", 8.0, 2000, 500));

        System.out.println("    Loaded 15 specialized items");
    }


    private void loadProducts() throws MyExceptions {
        System.out.println(" Loading Products...");

        Product robotArm = new Product(101, "Industrial Robot Arm RX-9", 15);
        robotArm.addOrEditMaterial(inventoryManager.getItemById(6), 5);  // Steel
        robotArm.addOrEditMaterial(inventoryManager.getItemById(13), 6); // Motors
        robotArm.addOrEditMaterial(inventoryManager.getItemById(11), 2); // Carbon Fiber
        robotArm.addOrEditMaterial(inventoryManager.getItemById(4), 8);  // Sensors
        robotArm.addOrEditMaterial(inventoryManager.getItemById(1), 1);  // Processor
        inventoryManager.addProduct(robotArm);

        Product drone = new Product(102, "Delivery Drone Quad-V", 25);
        drone.addOrEditMaterial(inventoryManager.getItemById(11), 3); // Carbon Fiber
        drone.addOrEditMaterial(inventoryManager.getItemById(13), 4); // Motors
        drone.addOrEditMaterial(inventoryManager.getItemById(5), 2);  // Battery
        drone.addOrEditMaterial(inventoryManager.getItemById(1), 1);  // Processor
        drone.addOrEditMaterial(inventoryManager.getItemById(4), 4);  // Sensors
        inventoryManager.addProduct(drone);

        Product smartConsole = new Product(103, "Smart Factory Console", 40);
        smartConsole.addOrEditMaterial(inventoryManager.getItemById(2), 2);  // Display
        smartConsole.addOrEditMaterial(inventoryManager.getItemById(1), 1);  // Processor
        smartConsole.addOrEditMaterial(inventoryManager.getItemById(3), 4);  // Memory
        smartConsole.addOrEditMaterial(inventoryManager.getItemById(7), 2);  // Aluminum
        smartConsole.addOrEditMaterial(inventoryManager.getItemById(10), 10); // ABS
        inventoryManager.addProduct(smartConsole);

        Product conveyor = new Product(104, "Automated Conveyor Belt", 10);
        conveyor.addOrEditMaterial(inventoryManager.getItemById(12), 20); // Rubber
        conveyor.addOrEditMaterial(inventoryManager.getItemById(6), 15);  // Steel
        conveyor.addOrEditMaterial(inventoryManager.getItemById(13), 2);  // Motors
        conveyor.addOrEditMaterial(inventoryManager.getItemById(15), 50); // Bearings
        inventoryManager.addProduct(conveyor);

        Product gateway = new Product(105, "IoT Edge Gateway", 120);
        gateway.addOrEditMaterial(inventoryManager.getItemById(10), 2);  // ABS
        gateway.addOrEditMaterial(inventoryManager.getItemById(1), 1);   // Processor
        gateway.addOrEditMaterial(inventoryManager.getItemById(3), 2);   // Memory
        gateway.addOrEditMaterial(inventoryManager.getItemById(8), 1);   // Copper Wire
        inventoryManager.addProduct(gateway);

        Product engine = new Product(106, "Titanium Jet Turbine", 5);
        engine.addOrEditMaterial(inventoryManager.getItemById(9), 10);  // Titanium
        engine.addOrEditMaterial(inventoryManager.getItemById(15), 100); // Bearings
        engine.addOrEditMaterial(inventoryManager.getItemById(4), 15);  // Sensors
        inventoryManager.addProduct(engine);

        System.out.println("    Loaded 6 advanced products with BoM");
    }


    private void loadProductLines() {
        System.out.println("  Loading Production Lines...");

        ProductLine line1 = new ProductLine(1, "Alpha Robotics Assembly", LineStatus.ACTIVE);
        line1.setManagerRating("Excellent");
        line1.setManagerNotes("Running smoothly at 98% efficiency.");
        inventoryManager.addProductLine(line1);

        ProductLine line2 = new ProductLine(2, "Beta Electronics PCB", LineStatus.ACTIVE);
        line2.setManagerRating("Very Good");
        line2.setManagerNotes("Recent calibration improved output speed.");
        inventoryManager.addProductLine(line2);

        ProductLine line3 = new ProductLine(3, "Gamma Heavy Machinery", LineStatus.ACTIVE);
        line3.setManagerRating("Good");
        line3.setManagerNotes("Awaiting new hydraulic press components.");
        inventoryManager.addProductLine(line3);

        ProductLine line4 = new ProductLine(4, "Delta Plastics & Molding", LineStatus.STOPPED);
        line4.setManagerRating("Poor");
        line4.setManagerNotes("Stopped due to raw material shortages (ABS).");
        inventoryManager.addProductLine(line4);

        ProductLine line5 = new ProductLine(5, "Omega Quality Control", LineStatus.MAINTENANCE);
        line5.setManagerRating("Fair");
        line5.setManagerNotes("Scheduled maintenance for sensor alignment.");
        inventoryManager.addProductLine(line5);

        ProductLine line6 = new ProductLine(6, "Zeta R&D Prototypes", LineStatus.ACTIVE);
        line6.setManagerRating("Excellent");
        line6.setManagerNotes("Testing new drone aerodynamic frames.");
        inventoryManager.addProductLine(line6);

        System.out.println("   ✓ Loaded 6 production lines");
    }


    private void loadTasks() throws MyExceptions {
        System.out.println("📋 Loading Tasks...");

        Product robotArm = inventoryManager.getProductById(101);
        Product drone = inventoryManager.getProductById(102);
        Product smartConsole = inventoryManager.getProductById(103);
        Product conveyor = inventoryManager.getProductById(104);
        Product gateway = inventoryManager.getProductById(105);
        Product engine = inventoryManager.getProductById(106);

        // --- COMPLETED TASKS ---
        Task t1 = new Task(1001, smartConsole, 15, "Global Tech Corp", LocalDateTime.now().minusDays(2));
        t1.setStatus(TaskStatus.COMPLETED);
        t1.setQuantitycompleted(15);
        t1.setCompletPercent(100.0);
        inventoryManager.addTask(t1);
        t1.setAssignedLine(inventoryManager.getProductLineById(2));
        inventoryManager.getProductLineById(2).archiveTask(t1);

        Task t2 = new Task(1002, conveyor, 5, "Logistics Pro LLC", LocalDateTime.now().minusDays(5));
        t2.setStatus(TaskStatus.COMPLETED);
        t2.setQuantitycompleted(5);
        t2.setCompletPercent(100.0);
        inventoryManager.addTask(t2);
        t2.setAssignedLine(inventoryManager.getProductLineById(3));
        inventoryManager.getProductLineById(3).archiveTask(t2);

        Task t3 = new Task(1003, gateway, 50, "Smart City Initiative", LocalDateTime.now().minusDays(1));
        t3.setStatus(TaskStatus.COMPLETED);
        t3.setQuantitycompleted(50);
        t3.setCompletPercent(100.0);
        inventoryManager.addTask(t3);
        t3.setAssignedLine(inventoryManager.getProductLineById(2));
        inventoryManager.getProductLineById(2).archiveTask(t3);

        // --- IN-PROGRESS / PENDING TASKS FOR ACTIVE LINES ---
        // Line 1: Alpha Robotics Assembly
        Task t4 = new Task(1004, robotArm, 10, "AutoMakers International", LocalDateTime.now().plusDays(14));
        t4.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t4);
        productionManager.addTaskToLine(1, 1004);

        Task t5 = new Task(1005, drone, 25, "AeroDelivery Express", LocalDateTime.now().plusDays(21));
        t5.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t5);
        productionManager.addTaskToLine(1, 1005);

        // Line 2: Beta Electronics
        Task t6 = new Task(1006, smartConsole, 30, "Future Innovations", LocalDateTime.now().plusDays(7));
        t6.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t6);
        productionManager.addTaskToLine(2, 1006);

        Task t7 = new Task(1007, gateway, 100, "IoT Networks Inc.", LocalDateTime.now().plusDays(10));
        t7.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t7);
        productionManager.addTaskToLine(2, 1007);

        // Line 3: Gamma Heavy Machinery
        Task t8 = new Task(1008, conveyor, 8, "Amazonia Warehouses", LocalDateTime.now().plusDays(30));
        t8.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t8);
        productionManager.addTaskToLine(3, 1008);

        // Line 6: Zeta R&D
        Task t9 = new Task(1009, engine, 2, "AeroSpace Dynamics", LocalDateTime.now().plusDays(45));
        t9.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t9);
        productionManager.addTaskToLine(6, 1009);

        Task t10 = new Task(1010, drone, 5, "Defense Tech Co.", LocalDateTime.now().plusDays(15));
        t10.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t10);
        productionManager.addTaskToLine(6, 1010);

        // --- TASKS ASSIGNED TO STOPPED/MAINTENANCE LINES ---
        Task t11 = new Task(1011, gateway, 20, "Local Distributors", LocalDateTime.now().plusDays(5));
        t11.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t11);
        productionManager.addTaskToLine(4, 1011); // Line 4 is stopped

        Task t12 = new Task(1012, robotArm, 3, "University Labs", LocalDateTime.now().plusDays(12));
        t12.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t12);
        productionManager.addTaskToLine(5, 1012); // Line 5 is maintenance

        // --- UNASSIGNED OR MORE PENDING TASKS ---
        Task t13 = new Task(1013, smartConsole, 8, "National Hospital Group", LocalDateTime.now().plusDays(20));
        t13.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t13);
        productionManager.addTaskToLine(2, 1013);

        Task t14 = new Task(1014, engine, 1, "Private Jet Customs", LocalDateTime.now().plusDays(60));
        t14.setStatus(TaskStatus.PENDING);
        inventoryManager.addTask(t14);
        productionManager.addTaskToLine(6, 1014);

        System.out.println("   ✓ Loaded 14 tasks with varying statuses");
    }


    private void printSummary() {
        System.out.println("📊 Data Summary:");
        System.out.println("   • Items: " + inventoryManager.items.size());
        System.out.println("   • Products: " + inventoryManager.products.size());
        System.out.println("   • Production Lines: " + inventoryManager.productsLines.size());
        System.out.println("   • Tasks: " + inventoryManager.tasks.size());

        long lowStock = inventoryManager.items.values().stream()
                .filter(i -> i.getQuantity() < i.getMinLimit())
                .count();
        System.out.println("   • Low Stock Items: " + lowStock);

        long activeLines = inventoryManager.productsLines.values().stream()
                .filter(l -> l.getStatus() == LineStatus.ACTIVE)
                .count();
        System.out.println("   • Active Lines: " + activeLines);
    }


    public static boolean isInventoryEmpty() {
        InventoryManager inv = InventoryManager.getInstance();
        return inv.items.isEmpty() &&
                inv.products.isEmpty() &&
                inv.productsLines.isEmpty() &&
                inv.tasks.isEmpty();
    }
}