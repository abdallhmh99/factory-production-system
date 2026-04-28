package com.project.model;



import com.project.MyExceptions;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Item implements Serializable {
    private int id;
    private String name;
    private String category; // مثلا: الكترونيات، ملابس...
    private double price;
    private int quantity;
    private int minLimit;
    private int reservedQuantity;



    public Item(int id, String name, String category, double price, int quantity, int minLimit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minLimit = minLimit;
        this.reservedQuantity = 0;
    }

    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public synchronized void reserve(int amount) throws MyExceptions {
        if (reservedQuantity + amount <= quantity) {
            this.reservedQuantity += amount;
        }else throw new MyExceptions("quantity not enough");
    }


    public synchronized void unreserve(int amount) {
        if (amount <= reservedQuantity) {
            this.reservedQuantity -= amount;
        } else {

            this.reservedQuantity = 0;
        }
    }


    public synchronized void consume(int amount) {

            this.reservedQuantity -= amount;

        this.quantity -= amount;
    }

    //---------------------------------Getters and Setters-------------------------------------------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public int getMinLimit() { return minLimit; }
    public void setMinLimit(int minLimit) { this.minLimit = minLimit; }

    @Override
    public String toString() {
        return String.format("ID:%d, %s, Qty:%d (Avail:%d), Price:%.1f",
                id, name, quantity, getAvailableQuantity(), price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}