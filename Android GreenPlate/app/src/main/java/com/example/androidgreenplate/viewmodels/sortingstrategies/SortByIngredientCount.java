package com.example.androidgreenplate.viewmodels.sortingstrategies;
import com.example.androidgreenplate.model.Recipe;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

public class SortByIngredientCount implements  RecipeSortingStrategy {
    @Override
    public List<Recipe> sort(List<Recipe> recipes) {
        Collections.sort(recipes, Comparator.comparingInt(recipe ->
                recipe.getRecipeIngredients().size()));
        return recipes;
    }
}
