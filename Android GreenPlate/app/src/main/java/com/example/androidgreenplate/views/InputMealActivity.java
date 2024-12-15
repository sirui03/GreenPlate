package com.example.androidgreenplate.views;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidgreenplate.model.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import com.example.androidgreenplate.R;

import com.example.androidgreenplate.model.Meal;
import com.example.androidgreenplate.model.MealsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.androidgreenplate.viewmodels.InputMealViewModel;

import java.util.ArrayList;
import java.util.List;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;

public class InputMealActivity extends AppCompatActivity {

    private EditText mealNameEditText;
    private EditText caloriesEditText;

    private RecyclerView mealsRecyclerView;
    private MealsAdapter mealsAdapter;

    private InputMealViewModel inputMealViewModel; //josephs
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_meal);
        inputMealViewModel = InputMealViewModel.getInstance();

        inputMealViewModel.initialize();

        // this looks at the livedata from the viewmodel
        inputMealViewModel.getSaveMealStatus().observe(this, saveMealStatus -> {
            String message = saveMealStatus.getErrorMessage();
            System.out.println(message);
            if (!TextUtils.isEmpty(message)) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        // linking UI components:
        mealNameEditText = findViewById(R.id.mealNameEditText);
        caloriesEditText = findViewById(R.id.caloriesEditText);
        TextView dailyCalorieIntakeLabelTextView = findViewById(
                R.id.dailyCalorieIntakeLabelTextView);
        TextView dailyCalorieGoalLabelTextView = findViewById(
                R.id.dailyCalorieGoalLabelTextView);
        TextView outputCaloriesTextView = findViewById(R.id.outputCaloriesTextView);
        TextView outputCalorieGoalTextView = findViewById(R.id.outputCalorieGoalTextView);
        TextView outputGender = findViewById(R.id.outputGender);
        TextView outputHeight = findViewById(R.id.outputHeight);
        TextView outputWeight = findViewById(R.id.outputWeight);


        Button submitMealButton = findViewById(R.id.submitMealButton);

        // setup bottom navigation
        setupBottomNavigationView(inputMealViewModel);

        inputMealViewModel.getUser().observe(this, user -> {
            outputGender.setText(String.valueOf(user.getGender()));
            outputHeight.setText(String.valueOf(user.getHeight()));
            outputWeight.setText(String.valueOf(user.getWeight()));
            outputCalorieGoalTextView.setText(String.valueOf(user.getGoal()));
        });


        // calculates and displays total cals consumed
        inputMealViewModel.getTotalCaloriesLiveData().observe(this, totalCalories -> {
            outputCaloriesTextView.setText(String.valueOf(totalCalories));
        });



        // gets a list of meals for the current user and displays them
        inputMealViewModel.getUserMealsLiveData().observe(this, userMeals -> {
            mealsRecyclerView = findViewById(R.id.mealsRecycler);
            mealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mealsAdapter = new MealsAdapter(userMeals);
            mealsRecyclerView.setAdapter(mealsAdapter);
        });



        Button submit = findViewById(R.id.submitMealButton);
        submit.setOnClickListener(v -> {
            EditText mealNameEditText = findViewById(R.id.mealNameEditText);
            String mealName = mealNameEditText.getText().toString().trim();
            EditText caloriesEditText = findViewById(R.id.caloriesEditText);
            String caloriesString = caloriesEditText.getText().toString().trim();

            inputMealViewModel.save(mealName, caloriesString);
        });

        Button userVisualization = findViewById(R.id.userVisualizationButton);
        Button mealVisualization = findViewById(R.id.mealVisualizationButton);
        userVisualization.setOnClickListener(v -> showUserChart());
        mealVisualization.setOnClickListener(v -> showMealChart());

    }
    private void setupBottomNavigationView(InputMealViewModel inputMealViewModel) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            item.setChecked(true);
            if (item.getItemId() == R.id.nav_homeActivity) {
                inputMealViewModel.setSaveMealStatus();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_ingredientsActivity) {
                inputMealViewModel.setSaveMealStatus();
                Intent intent = new Intent(this, IngredientsActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_inputMealActivity) {
                inputMealViewModel.setSaveMealStatus();
                Intent intent = new Intent(this, InputMealActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_recipeActivity) {
                inputMealViewModel.setSaveMealStatus();
                Intent intent = new Intent(this, RecipeActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_shoppingListActivity) {
                inputMealViewModel.setSaveMealStatus();
                Intent intent = new Intent(this, ShoppingListActivity.class);
                startActivity(intent);
                finish();
            } else if (item.getItemId() == R.id.nav_userInfoActivity) {
                inputMealViewModel.setSaveMealStatus();
                Intent intent = new Intent(this, UserInfoActivity.class);
                startActivity(intent);
                finish();
            }
            return false;
        });
    }

    private void showUserChart() {
        Dialog userChartDialog = new Dialog(this);
        userChartDialog.setContentView(R.layout.dialog_user_chart);


        BarChart barChart = userChartDialog.findViewById(R.id.line_chart);
        configureUserChart(barChart, inputMealViewModel.getUserListLiveData().getValue());

        Button closeButton = userChartDialog.findViewById(R.id.close_button);
        closeButton.setOnClickListener(view -> userChartDialog.dismiss());

        userChartDialog.show();
        userChartDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    private void configureUserChart(BarChart barChart, List<User> users) {
        // Create a list of Entry objects for the line chart
        List<BarEntry> entries = new ArrayList<>();
        int avgHeight = 0;
        int avgWeight = 0;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            avgHeight += user.getHeight();
            System.out.println("This is a line of testing");
            avgWeight += user.getWeight();
        }
        avgHeight /= users.size();
        avgWeight /= users.size();
        User currentUser = inputMealViewModel.getUser().getValue();
        int userHeight = currentUser.getHeight();
        int userWeight = currentUser.getWeight();

        entries.add(new BarEntry(0, avgHeight));
        entries.add(new BarEntry(1, currentUser.getHeight()));
        entries.add(new BarEntry(2, avgWeight));
        entries.add(new BarEntry(3, currentUser.getWeight()));

        BarDataSet dataSet = new BarDataSet(entries, "Height and Weight");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Set to 1 to match the integer positions
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"Average Height", "User Height", "Average Weight", "User Weight"}));

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        barChart.invalidate();
    }

    private void showMealChart() {
        Dialog mealChartDialog = new Dialog(this);
        mealChartDialog.setContentView(R.layout.dialog_meal_chart);

        LineChart mealChart = mealChartDialog.findViewById(R.id.line_chart);
        configureMealChart(mealChart, inputMealViewModel.getUserMealsLiveData().getValue());

        Button closeButton = mealChartDialog.findViewById(R.id.close_button);
        closeButton.setOnClickListener(view -> mealChartDialog.dismiss());

        mealChartDialog.show();
        mealChartDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void configureMealChart(LineChart lineChart, List<Meal> meals) {
        // Create a list of Entry objects for the line chart
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.get(i);
            // Assuming meal.getCalories() returns a float or int value
            entries.add(new Entry(i, meal.getCalories()));
        }

        // Create a LineDataSet from the list of entries
        LineDataSet dataSet = new LineDataSet(entries, "Calories");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        // Creating a LineData object from the data set
        LineData lineData = new LineData(dataSet);

        // Assigning the LineData to the LineChart
        lineChart.setData(lineData);

        // Customizing x-axis to show meal names
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < meals.size()) {
                    return meals.get(index).getMealName();
                }
                // Return empty string or some placeholder if the index is out of bounds
                return "";
            }
        });

        // Customizing y-axis, if needed
        YAxis leftAxis = lineChart.getAxisLeft();
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false); // No right axis

        // Refreshing the chart
        lineChart.invalidate();
    }

}
