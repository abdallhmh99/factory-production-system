package com.project.model;

import com.project.MyExceptions;
import com.project.controller.FileManager;
import com.project.controller.InventoryManager;

import java.io.Serializable;
import java.util.*;

import static com.project.model.TaskStatus.CANCELED;

public class ProductLine implements Runnable , Serializable {
    private int id;
    private String name;
    private volatile  LineStatus status; // نشط، متوقف، صيانة
    private String managerRating = "N/A";
    private String managerNotes = "new";
    private volatile double completPercentForCurrentTask;

    private Queue<Task> tasks;
    private List<Task> completedTasks = new LinkedList<>();
    private List<Product> completedProducts = new LinkedList<>();





    public ProductLine(int id, String name, LineStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.tasks = new LinkedList<>();
    }

    @Override
    public void run() {
        InventoryManager inv = InventoryManager.getInstance();
        int notMMaterialsForTask = 0;

        while (true) {

            Task task = null;

            try {
                if (status != LineStatus.ACTIVE || tasks.isEmpty()) {
                    Thread.sleep(1000);
                    continue;
                }


                task = tasks.peek();

                // تخطي المهام المكتملة مسبقاً
                if (task.getStatus() == TaskStatus.COMPLETED ||
                    task.getQuantitycompleted() >= task.getQuantityRequired()) {
                    tasks.poll();
                    archiveTask(task);
                    setCompletPercentForCurrentTask(0);
                    continue;
                }

                // التحقق من توفر المواد
                if (!inv.reserveMaterialsForTask(task)){
                    System.out.println("Line " + name + ": Not enough materials for Task " + task.getId() + ". Waiting...");
                    notMMaterialsForTask++;
                    Thread.sleep(1000);

                    if (notMMaterialsForTask >= 3) {
                        task.setStatus(TaskStatus.CANCELED);
                        tasks.poll();
                        System.out.println("Line " + name + ": Task " + task.getId() + " CANCELED due to lack of materials.");
                        notMMaterialsForTask = 0;
                    }
                    continue;
                }
                notMMaterialsForTask = 0;

                task.setStatus(TaskStatus.IN_PROGRESS);
                Product product = inv.getProductById(task.getProduct().getId());

                int completedQuantyity = task.getQuantitycompleted();

                while (completedQuantyity < task.getQuantityRequired()){
                    // فحص الإيقاف
                    if (task.getStatus() == CANCELED || status == LineStatus.STOPPED || status == LineStatus.MAINTENANCE){
                        if (status == LineStatus.STOPPED || status == LineStatus.MAINTENANCE) {
                            // تحرير المواد المتبقية عند التوقف المؤقت
                            inv.releaseReservedMaterials(task, completedQuantyity);
                            // إعادة حالة المهمة للانتظار
                            task.setStatus(TaskStatus.PENDING);
                            break;
                        }
                        inv.releaseReservedMaterials(task, task.getQuantityRequired() - completedQuantyity);
                        tasks.poll();
                        archiveTask(task);
                        break;
                    }

                    // الإنتاج
                    completedQuantyity++;
                    task.setQuantitycompleted(completedQuantyity);
                    inv.editProductQuantity(product.getId(), 1);
                    inv.completeProductionForSingleUnit(task);

                    // تحديث النسبة
                    if (task.getQuantityRequired() > 0) {
                        double completPercent = (completedQuantyity * 100.0) / task.getQuantityRequired();
                        task.setCompletPercent(completPercent);
                        setCompletPercentForCurrentTask(completPercent);
                    }

                    Thread.sleep(400); // محاكاة الوقت - سرعة أعلى للعرض

                    // الانتهاء
                    if (completedQuantyity >= task.getQuantityRequired()) {
                        task.setStatus(TaskStatus.COMPLETED);
                        tasks.poll();
                        archiveTask(task);
                        setCompletPercentForCurrentTask(0);
                    }
                }

            } catch (InterruptedException e) {

                // عند مقاطعة الخيط (Restart/Stop)، يجب تحرير المواد
                System.out.println("⚠️ Line " + name + " Interrupted. Releasing resources...");

                if (task != null && task.getStatus() == TaskStatus.IN_PROGRESS) {

                    inv.releaseReservedMaterials(task, task.getQuantitycompleted());

                    task.setStatus(TaskStatus.PENDING);
                }

                return;

            } catch (MyExceptions e) {
                System.err.println("Error in Line " + name + ": " + e.getMessage());
                FileManager.logError("Production Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("CRITICAL ERROR in Line " + name + ": " + e.getMessage());
                e.printStackTrace();
                try { Thread.sleep(3000); } catch (InterruptedException ex) {}
            }
        }
    }

    public void addTask(Task task) {
        tasks.add(task);
    }


    public void archiveTask(Task task) {
        completedTasks.add(task);

        if (!completedProducts.contains(task.getProduct())) {
            completedProducts.add(task.getProduct());
        }
    }


    //--------------------------Getters and Setters-----------------------------
    public int getId() { return id; }
    public String getName() { return name; }
    public LineStatus getStatus() { return status; }
    public void setStatus(LineStatus status) { this.status = status; }
    public Queue<Task> getTasks() { return tasks; }
    public List<Task> getCompletedTasks() { return completedTasks; }
    public List<Product> getProducts() { return completedProducts; }
    public String getManagerRating() { return managerRating; }
    public void setManagerRating(String managerRating) { this.managerRating = managerRating; }
    public String getManagerNotes() { return managerNotes; }
    public void setManagerNotes(String managerNotes) { this.managerNotes = managerNotes; }
    public void setCompletPercentForCurrentTask(double completPercent) { this.completPercentForCurrentTask = completPercent; }
    public double getCompletPercentForCurrentTask(){ return completPercentForCurrentTask; }



    //----------------------------------------------------------------------------

    @Override
    public String toString() {

        int taskCount = (tasks != null) ? tasks.size() : 0;
        return String.format("ID:%d, Name:%s, Status:%s, Pending_Tasks:%d",
                id, name, status, taskCount);
    }


}