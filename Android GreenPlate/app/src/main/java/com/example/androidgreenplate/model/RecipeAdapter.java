package com.example.androidgreenplate.model;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import com.example.androidgreenplate.R;
import com.example.androidgreenplate.viewmodels.RecipeViewModel;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * RecipeAdapter is a RecyclerView adapter that displays a list of recipes in a RecyclerView.
 * It binds the recipe data to the views in each item of the RecyclerView.
 * The adapter handles the creation and recycling of views as the user scrolls.
 * It also provides methods to update the recipe data and notify the RecyclerView of changes.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private List<Recipe> recipeList;
    private RecipeViewModel recipeViewModel = RecipeViewModel.getInstance();
    private Recipe recipe;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipeNameTextView.setText(recipe.getRecipeName());
        holder.ingredientCountTextView.setText(String.valueOf(recipe.getRecipeIngredients().size())
                + " ingredients");

        boolean expandable = false;
        System.out.println("fuck");
        System.out.println(position);
        System.out.println(recipe);
        if (recipeViewModel.hasEnoughIngredients(recipe)) {
            System.out.println(true);
            expandable = true;
        }
        System.out.println(expandable);
        if (expandable) {
            holder.addButton.setEnabled(true);
            holder.recipeNameTextView.setOnClickListener(v -> {
                holder.addDetailTextViews(recipe.getRecipeIngredients());
                holder.toggleDetails();
            });
        } else {
            holder.addButton.setEnabled(false);
            holder.recipeNameTextView.setOnClickListener(null);
        }
        // Add any additional binding logic for ingredient availability indication
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipeList = recipes;
        notifyDataSetChanged();
    }

    public List<Recipe> getRecipeList() {
        return this.recipeList;
    }



    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeNameTextView;
        private TextView ingredientCountTextView;
        private Button addButton;
        private LinearLayout detailsContainer;
        private boolean isExpanded = false;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            ingredientCountTextView = itemView.findViewById(R.id.ingredientCountTextView);

            addButton = itemView.findViewById(R.id.addButton);
            detailsContainer = itemView.findViewById(R.id.detailsContainer);


            addButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                System.out.println(position);
                if (position != RecyclerView.NO_POSITION) {
                    recipe = recipeList.get(position);
                    System.out.println(recipe);
                    if (recipeViewModel.hasEnoughIngredients(recipe)) {
                        System.out.println(true);
                        addDetailTextViews(recipe.getRecipeIngredients());
                        toggleDetails();
                    }
                }

            });

            recipeNameTextView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                System.out.println(position);
                if (position != RecyclerView.NO_POSITION) {
                    recipe = recipeList.get(position);
                    System.out.println(recipe);
                    if (recipeViewModel.hasEnoughIngredients(recipe)) {
                        System.out.println(true);
                        addDetailTextViews(recipe.getRecipeIngredients());
                        toggleDetails();
                    }
                }

            });

        }
        private void toggleDetails() {
            // This method should now ensure visibility toggling
            if (isExpanded) {
                detailsContainer.setVisibility(View.GONE);
            } else {
                detailsContainer.setVisibility(View.VISIBLE);
            }
            isExpanded = !isExpanded;
        }
        void addDetailTextViews(List<Ingredient> ingredients) {
            detailsContainer.removeAllViews(); // Clear previous views if any
            for (int i = 0; i < ingredients.size(); i++) {

                TextView nameTextView = new TextView(detailsContainer.getContext());
                TextView quantityTextView = new TextView(detailsContainer.getContext());
                nameTextView.setText("Ingredient Name: " + ingredients.get(i).getName());
                nameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                quantityTextView.setText("Quantity:" + (ingredients.get(i).getQuantity()));
                quantityTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                detailsContainer.addView(nameTextView);
                detailsContainer.addView(quantityTextView);
            }
        }
    }

}