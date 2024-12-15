package com.example.androidgreenplate.model;
import java.util.ArrayList;

public class ShoppingList {

    private ArrayList<Ingredient> list;

    public ShoppingList() {
        this.list = new ArrayList<>();
    }

    public void addIngredient(Ingredient ingredient) {
        list.add(ingredient);
    }

    public void removeIngredient(Ingredient ingredient) {
        list.remove(ingredient);
    }

    public ArrayList<Ingredient> getList() {
        return list;
    }

    public void setList(ArrayList<Ingredient> list) {
        this.list = list;
    }

    public int getQuantity() {
        return list.size();
    }

    public void clearList() {
        list.clear();
    }
}
