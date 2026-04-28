package com.project.controller;

import com.project.model.LineStatus;
import com.project.model.ProductLine;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadManager {
    private static ThreadManager instance;
    private Map<Integer, Thread> lineThreads = new ConcurrentHashMap<>();

    private ThreadManager() {}

    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            instance = new ThreadManager();
        }
        return instance;
    }


    public synchronized void startLineThread(ProductLine line) {
        if (line == null) return;

        int lineId = line.getId();
        Thread existingThread = lineThreads.get(lineId);

        if (existingThread != null && existingThread.isAlive()) {
            return;
        }

        if (existingThread != null && !existingThread.isAlive()) {
            lineThreads.remove(lineId);
        }

        Thread thread = new Thread(line);
        thread.setName("ProductLine-" + line.getName() + "-ID" + lineId);
        thread.setDaemon(true);
        thread.start();

        lineThreads.put(lineId, thread);
        System.out.println(" Started thread for: " + line.getName() + " (ID: " + lineId + ")");
    }


    public void restartLineThread(ProductLine line) {
        if (line == null) return;

        System.out.println(" Restarting sequence for: " + line.getName());


        stopLineThread(line.getId());


        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        startLineThread(line);
    }


    public void startAllActiveLines() {
        InventoryManager inv = InventoryManager.getInstance();

        System.out.println(" Starting all active production lines...");
        int startedCount = 0;

        for (ProductLine line : inv.productsLines.values()) {
            if (line.getStatus() == LineStatus.ACTIVE) {
                startLineThread(line);
                startedCount++;
            }
        }

        System.out.println(" Started " + startedCount + " production line threads");
    }


    public void stopLineThread(int lineId) {
        Thread thread = lineThreads.get(lineId);
        if (thread != null) {
            System.out.println(" Stopping thread for line ID: " + lineId);
            thread.interrupt();

            try {
                thread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (thread.isAlive()) {
                System.err.println(" Warning: Thread for line " + lineId + " did not stop gracefully.");
            } else {
                System.out.println(" Thread for line " + lineId + " is now DEAD.");
            }

            lineThreads.remove(lineId);
        }
    }


    public String getThreadStatus(int lineId) {
        Thread thread = lineThreads.get(lineId);
        if (thread == null) {
            return "Not Started";
        }
        return thread.isAlive() ? "Running" : "Dead";
    }

   }