package com.example.androidgreenplate.model;

public class Day {

    private int dayNumber;
    private int consumedCalories;

    public Day(int dayNumber) {
        this.dayNumber = dayNumber;
    }



    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getConsumedCalories() {
        return consumedCalories;
    }

    public void setConsumedCalories(int consumedCalories) {
        this.consumedCalories = consumedCalories;
    }


    public void addMealCalories(int calories) {
        this.consumedCalories += calories;
    }

}
