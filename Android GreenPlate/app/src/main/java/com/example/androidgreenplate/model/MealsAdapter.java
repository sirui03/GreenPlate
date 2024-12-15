package com.example.androidgreenplate.model;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import com.example.androidgreenplate.R;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


// this class is needed to use the recycler view to show the meals
// test using user: aaa@aaa.com Pass: aaaaaa
public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MealViewHolder> {

    private List<Meal> mealsList;

    public MealsAdapter(List<Meal> mealsList) {
        this.mealsList = mealsList;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_layout, parent, false);
        return new MealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealsList.get(position);
        holder.mealNameTextView.setText(meal.getMealName());
        holder.caloriesTextView.setText(String.valueOf(meal.getCalories()) + " cal");
    }

    @Override
    public int getItemCount() {
        return mealsList.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        private TextView mealNameTextView;
        private TextView caloriesTextView;
        public TextView getMealNameTextView() {
            return mealNameTextView;
        }
        public TextView getCaloriesTextView() {
            return caloriesTextView;
        }

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealNameTextView = itemView.findViewById(R.id.mealNameTextView);
            caloriesTextView = itemView.findViewById(R.id.caloriesTextView);
        }
    }
}
