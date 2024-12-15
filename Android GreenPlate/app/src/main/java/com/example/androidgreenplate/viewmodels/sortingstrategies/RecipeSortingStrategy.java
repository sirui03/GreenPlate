package com.example.androidgreenplate.viewmodels.sortingstrategies;
import com.example.androidgreenplate.model.Recipe;

import java.util.List;

public interface RecipeSortingStrategy {
    List<Recipe> sort(List<Recipe> recipes);
}
