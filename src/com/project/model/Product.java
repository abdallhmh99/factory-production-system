package com.project.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Product implements Serializable {
    private int id;
    private String name;
    private int quantity;
    // مفتاح الخريطة هو المادة الخام، والقيمة هي الكمية المطلوبة منها لإنتاج وحدة واحدة
    private Map<Item, Integer> materials;


    public Product(int id, String name , int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.materials = new HashMap<>();
    }


    public void addOrEditMaterial(Item item, int quantityNeeded) {
        if (quantityNeeded == 0) {
            removeMaterials(item);
        }else
        materials.put(item, quantityNeeded);

    }
    public  void removeMaterials(Item item){
        materials.remove(item);
    }
    //-------------------------getter and setter-----------------------------

    public void setName(String name) { this.name = name; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getId() { return id; }
    public String getName() { return name; }

    public int getQuantity() { return quantity; }

    public Map<Item, Integer> getMaterials() { return materials; }

    @Override
    public String toString() {

        StringBuilder matStr = new StringBuilder("[");
        if (materials != null && !materials.isEmpty()) {
            materials.forEach((item, qty) -> matStr.append(item.getName()).append(":").append(qty).append("|"));

            if (matStr.length() > 1) matStr.setLength(matStr.length() - 1);
        }
        matStr.append("]");

        return String.format("ID:%d, Name:%s, Stock:%d, Materials:%s",
                id, name, quantity, matStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}