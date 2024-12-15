package com.example.androidgreenplate.model;


public class Ingredient {


    enum Type {
        Fruit,
        Vegetable,
        Grain,
        Protein,
        Dairy,
        Fat;
    }
    private String id;

    private String name;
    private int quantity;
    private int calories;
    private String userId;

    public Ingredient() { }

    public Ingredient(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Ingredient(String name, Integer quantity, int calories) {
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
    }

    public Ingredient(String id, String name, int quantity, int calories) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
    }

    public Ingredient(String id, String userId, String name, int quantity, int calories) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
    }

    public String getUserId() {
        return userId;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getQuantity() {
        return quantity;
    }
    public int getCalories() {
        return calories;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setCalories(int calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return this.name;
    }

}