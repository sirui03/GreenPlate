package com.example.androidgreenplate.model;

public class User {
    public int getCalsConsumed() {
        return calsConsumed;
    }

    public void setCalsConsumed(int calsConsumed) {
        this.calsConsumed = calsConsumed;
    }

    public enum ActivityLevel {
        SEDENTARY(1.2),
        LIGHTLY_ACTIVE(1.375),
        MODERATELY_ACTIVE(1.55),
        VERY_ACTIVE(1.725),
        EXTRA_ACTIVE(1.95);

        private final double multiplier;

        ActivityLevel(double multiplier) {
            this.multiplier = multiplier;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }
    private String gender;
    private int age;
    private int height;
    private int weight;
    private int goal;

    private int calsConsumed;

    public User(String gender, int height, int weight) {
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        //automatically calculated the daily calorie goal upon object creation
        this.goal = calculateDailyCalorieGoal();
    }

    public User() {
        this("Unknown", 0, 0);
    }

    public int getGoal() {
        calculateAndSetGoal();
        return goal;
    }

    public String getGender() {
        return gender;
    }
    public int getAge() {
        return age;
    }
    public int getHeight() {
        return height;
    }
    public int getWeight() {
        return weight;
    }


    public void setGoal() {
        this.goal = goal;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }


    public void calculateAndSetGoal() {
        this.goal = calculateDailyCalorieGoal();
    }

    private int calculateDailyCalorieGoal() {
        // Calculate BMR based on sex. This is a simplified formula.
        double bmr;
        double calGoal;
        try {
            if (weight == 0 || height == 0) {
                return 0;
            }
            if (gender.equals("Male") || gender.equals("male") || gender.equals("MALE")) {
                bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
            } else {
                bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
            }

            // Adjust BMR based on activity level. This factor will vary.
            return  (int) (bmr * ActivityLevel.MODERATELY_ACTIVE.getMultiplier());

        } catch (Exception e) {
            return 0;
        }

    }
}
