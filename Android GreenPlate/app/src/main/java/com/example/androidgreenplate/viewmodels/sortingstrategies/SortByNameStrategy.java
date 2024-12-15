package com.example.androidgreenplate.viewmodels.sortingstrategies;
import com.example.androidgreenplate.model.Recipe;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortByNameStrategy implements RecipeSortingStrategy {
    @Override
    public List<Recipe> sort(List<Recipe> recipes) {
        Collections.sort(recipes, Comparator.comparing(recipe -> recipe.getRecipeName()));
        return recipes;
    }

}
