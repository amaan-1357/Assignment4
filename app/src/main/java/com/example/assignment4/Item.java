package com.example.assignment4;

public class Item {
    private String userId;
    private String name;
    private int quantity;
    private double price;

    public Item() { }

    public Item(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public Item(String userId, String name, int quantity, double price) {
        this.userId = userId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getUserId() {
        return userId;
    }

}
