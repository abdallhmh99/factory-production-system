package com.project.controller;

import com.project.MyExceptions;
import com.project.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryManager implements Serializable {
    public  Map<Integer, Item> items ;
    public  Map<Integer, Product> products ;
    public  Map<Integer, ProductLine> productsLines ;
    public  Map<Integer, Task> tasks;
    private static InventoryManager instance;

    private InventoryManager() {
        items = new ConcurrentHashMap<>();
        products = new ConcurrentHashMap<>();
        productsLines = new ConcurrentHashMap<>();
        tasks = new ConcurrentHashMap<>();
    }

    public static synchronized InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();

        }
        return instance;
    }


    //---------------------------------تعدد المهام----------------------------

    public synchronized boolean reserveMaterialsForTask(Task task) throws MyExceptions {
        Product product = task.getProduct();
        Map<Item, Integer> materials = product.getMaterials();

        int remainingToProduce = task.getQuantityRequired() - task.getQuantitycompleted();
        if (remainingToProduce <= 0) return true;

        for (Map.Entry<Item, Integer> entry : materials.entrySet()) {
            int itemId = entry.getKey().getId();
            Item realItem = items.get(itemId);

            if (realItem == null) {
                throw new MyExceptions("Critical Data Error: Item ID " + itemId + " not found in main inventory!");
            }

            int requiredQtyForMaterial = entry.getValue() * remainingToProduce;

            if (requiredQtyForMaterial > realItem.getAvailableQuantity()) {
                System.out.println("Fails to reserve: Item " + realItem.getName() +
                        " Needs: " + requiredQtyForMaterial +
                        " Avail: " + realItem.getAvailableQuantity());
                return false;
            }
        }

        for (Map.Entry<Item, Integer> entry : materials.entrySet()) {
            int itemId = entry.getKey().getId();
            int requiredQtyForMaterial = entry.getValue() * remainingToProduce;
            items.get(itemId).reserve(requiredQtyForMaterial);
        }

        return true;
    }

    public synchronized void completeProductionForSingleUnit(Task task) {
        Product product = task.getProduct();
        Map<Item, Integer> materials = product.getMaterials();

        for (Map.Entry<Item, Integer> entry : materials.entrySet()) {
            int itemId = entry.getKey().getId();
            int amountPerUnit = entry.getValue();

            Item realItem = items.get(itemId);
            if (realItem != null) {
                realItem.consume(amountPerUnit);
            }
        }
    }

    public synchronized void releaseReservedMaterials(Task task, int quantityToReleaseFor) {
        if (quantityToReleaseFor <= 0) return;

        Product product = task.getProduct();
        Map<Item, Integer> materials = product.getMaterials();

        for (Map.Entry<Item, Integer> entry : materials.entrySet()) {
            int itemId = entry.getKey().getId();
            int materialPerUnit = entry.getValue();
            int totalToRelease = quantityToReleaseFor * materialPerUnit;

            Item realItem = items.get(itemId);
            if (realItem != null) {
                realItem.unreserve(totalToRelease);
            }
        }
    }

    //-------------------------------product----------------------------

    public void addProduct(Product product) throws MyExceptions {
        if (products.containsKey(product.getId())) {
            throw new MyExceptions("Product with ID " + product.getId() + " already exists!");
        }

        products.put(product.getId(), product);
        System.out.println("Product added: " + product.getName());
    }

    public synchronized void editProductQuantity(int idProduct, int amount) throws MyExceptions {
        Product product = products.get(idProduct);

        if (product == null) {

            throw new MyExceptions("محاولة تعديل منتج غير موجود، رقم المعرف: " + idProduct);
        }

        if (product.getQuantity() + amount < 0) {

            throw new MyExceptions("الكمية غير كافية للمنتج: " + product.getName());
        }

        product.setQuantity(product.getQuantity() + amount);
    }




    public List<Product> searchProductByName(String name) {
        name = name.toLowerCase().trim();
        List<Product> result = new ArrayList<>();

        for (Product currentProduct: products.values()) {

            if (currentProduct.getName().toLowerCase().equals(name)) {
                result.add(currentProduct);
            }
        }
        return result;
    }

    public void showProducts(){
        int i = 1;
        for (Product product : products.values()){
            System.out.print(1+'-');
            System.out.println(product.toString());
            System.out.println("");
            i++;
        }
    }





    //---------------------------productLine--------------------------------

    public void addProductLine (ProductLine productLine){
        productsLines.put(productLine.getId(),productLine);

        if (productLine.getStatus() == LineStatus.ACTIVE) {
            ThreadManager.getInstance().startLineThread(productLine);
        }

    }


    public synchronized int generateProductLineId() {
        int maxId = 0;

        for (Integer id : productsLines.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }

        return maxId + 1;
    }

    public void updateLineEvaluation (int lineId,String rating,String notes) {
        ProductLine productLine = getProductLineById(lineId);
        productLine.setManagerNotes(notes);
        productLine.setManagerRating(rating);
    }

    public List<ProductLine> searchProductLineByName(String name) {
        name = name.toLowerCase().trim();
        List<ProductLine> result = new ArrayList<>();

        for (ProductLine currentProductLine: productsLines.values()) {
            if (currentProductLine.getName().equals(name)) {
                result.add(currentProductLine);
            }
        }
        return result;
    }

    //-------------------------------task-----------------------------------

    public void addTask(Task task) throws MyExceptions {
        if (task.getProduct() == null) {
            throw new MyExceptions("لا يمكن إضافة مهمة لمنتج غير موجود!");
        }
        if (task.getQuantityRequired() <= 0) {
            throw new MyExceptions("كمية المهمة يجب أن تكون أكبر من صفر!");
        }
        tasks.put(task.getId(), task);
    }

    public List<Task> searchTaskBycustomerName(String customerName) {
        customerName = customerName.toLowerCase().trim();
        List<Task> result = new ArrayList<>();

        for (Task currentTask: tasks.values()) {
            if (currentTask.getCustomerName().equals(customerName)) {
                result.add(currentTask);
            }
        }
        return result;
    }

    public Task searchTaskById(int id) {
        return tasks.get(id);
    }

    //--------------------------Item-------------------------------------------

    public void addItem(Item item) throws MyExceptions {
        if (items.containsKey(item.getId())) {
            throw new MyExceptions("Item with ID " + item.getId() + " already exists!");
        }
        if (item.getPrice() < 0) {
            throw new MyExceptions("Item price cannot be negative!");
        }
        if (item.getQuantity() < 0) {
            throw new MyExceptions("Item quantity cannot be negative!");
        }
        if (item.getMinLimit() < 0) {
            throw new MyExceptions("Item minimum limit cannot be negative!");
        }
        items.put(item.getId(), item);
    }


    public void removeItem(int id){
        items.remove(id);
    }



    public List<Item> searchItemByName (String name) {
        name = name.toLowerCase().trim();
        List<Item> result = new ArrayList();

        for (Item currentItem : items.values()){
            if (currentItem.getName().equals(name)){
                result.add(currentItem);
            }
        }
        return result;
    }

    public List<Item> searchItemBycategory (String category) {
        category = category.toLowerCase().trim();
        List<Item> result = new ArrayList();

        for (Item currentItem : items.values()){
            if (currentItem.getCategory().equals(category)){
                result.add(currentItem);
            }
        }
        return result;
    }

    public List<Item> searchItemByquantity (int a) {
        List<Item> nonitem = new ArrayList();
        List<Item> yesitem = new ArrayList();

        for (Item currentItem : items.values()){
            if (currentItem.getQuantity()==0){
                nonitem.add(currentItem);
            } else {
                yesitem.add(currentItem);
            }
        }
        if (a == 0) {
            return nonitem;
        }return yesitem;

    }

    public List<Item> searchItemByminquantity () {
        List<Item> nonitem = new ArrayList();

        for (Item currentItem : items.values()){
            if (currentItem.getQuantity() < currentItem.getMinLimit()){
                nonitem.add(currentItem);
            }
        }
        return nonitem;
    }

    public void showItem(){
        int i = 1;

        for (Item item : items.values()){
            System.out.print(1+'-');
            System.out.println(item.toString());
            System.out.println("");
            i++;
        }
    }

    //----------------------------getter and setter------------------------------------

    public Item getItemById(int id){
        return items.get(id);
    }
    public Product getProductById(int id){
        return products.get(id);
    }
    public ProductLine getProductLineById(int id){
        return productsLines.get(id);
    }
    public Task getTaskById(int id){
        return tasks.get(id);
    }

}
