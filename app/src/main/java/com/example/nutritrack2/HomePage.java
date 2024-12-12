package com.example.nutritrack2;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.nutritrack2.databinding.ActivityHomePageBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.JsonSyntaxException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.Executors;
import com.google.gson.Gson;

public class HomePage extends AppCompatActivity {

    ActivityHomePageBinding binding;
    double calories = 0.0;
    double carbohydrate = 0.0;
    double protein = 0.0;
    double fat = 0.0;
    double sugar = 0.0;
    double trans_fat = 0.0;
    double saturated_fat = 0.0;
    FoodAdapter foodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the user data from the intent
        Intent intent = getIntent();
        String userJson;
        if (intent.hasExtra("user_profile")){
            userJson = intent.getStringExtra("user_profile");

        }else if(intent.hasExtra("user_search")){
            userJson = intent.getStringExtra("user_search");
        }else{
            userJson = intent.getStringExtra("user");
        }

        // Set the username
        binding.tvUsername.setText(User.fromJson(userJson).getNameAndLastName());
        // Set the date
        binding.tvDate.setText(dateFormatter());

        if (userJson != null) {
            try {
                User user = User.fromJson(userJson);
                List<Food> personalizedList = user.getPersonalized_List();

                if (personalizedList != null) {
                    for (Food f : personalizedList) {
                        calories += f.getFoodServings().getCalories();
                        carbohydrate += f.getFoodServings().getCarbohydrate();
                        protein += f.getFoodServings().getProtein();
                        fat += f.getFoodServings().getFat();
                        sugar += f.getFoodServings().getSugar();
                        trans_fat += f.getFoodServings().getTrans_fat();
                        saturated_fat += f.getFoodServings().getSaturated_fat();
                    }
                }


                // Set the current calorie intake
                binding.tvCurrent.setText(String.valueOf((int) calories));


                // Set the goal based on the user's attributes
                binding.tvGoal.setText(String.valueOf((int) (calculateCalorieGoal(user.getUserAttribute().getGender(),
                        user.getUserAttribute().getWeight(), user.getUserAttribute().getHeight(),
                        user.getUserAttribute().getAge()))));

                binding.tvRemaining.setText(String.valueOf((int) (calculateCalorieGoal(user.getUserAttribute().getGender(),
                        user.getUserAttribute().getWeight(), user.getUserAttribute().getHeight(),
                        user.getUserAttribute().getAge()) - calories)));

                // Set the macro chart
                ArrayList<PieEntry> intake = new ArrayList<>();
                intake.add(new PieEntry((int) protein, "Protein")); // Add label "Protein"
                intake.add(new PieEntry((int) carbohydrate, "Carbohydrate")); // Add label "Carbohydrate"
                intake.add(new PieEntry((int) fat, "Fat")); // Add label "Fat"
                intake.add(new PieEntry((int) sugar, "Sugar")); // Add label "Sugar"

                // Add more entries for other macros
                PieDataSet pieDataSet = new PieDataSet(intake, "Intake");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieDataSet.setValueTextColor(Color.BLACK);
                pieDataSet.setValueTextSize(12f);

                // Customize the legend
                Legend legend = binding.macroChart.getLegend();
                legend.setForm(Legend.LegendForm.CIRCLE);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(false);

                PieData pieData = new PieData(pieDataSet);
                binding.macroChart.setData(pieData);
                binding.macroChart.getDescription().setEnabled(false);
                binding.macroChart.setCenterText("Macros");
                binding.macroChart.animate();


                // Set the profile button
                binding.btnProfile.setOnClickListener(v -> {
                    Intent i = new Intent(HomePage.this, Profile.class);
                    //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("user", new Gson().toJson(user));
                    startActivity(i);
                });

                binding.btnAddFood.setOnClickListener(v -> {
                    Intent i2 = new Intent(HomePage.this, SearchFood.class);
                    //i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i2.putExtra("user", new Gson().toJson(user));
                    startActivity(i2);
                });


                // Set the food items RecyclerView
                personalizedList = user.getPersonalized_List();
                if (personalizedList != null) {
                    // Initialize RecyclerView
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    binding.foodItemsRecyclerView.setLayoutManager(layoutManager);

                    List<Food> finalPersonalizedList = personalizedList;
                    foodAdapter = new FoodAdapter(personalizedList, new FoodAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Food food) {
                            Handler handler = new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(Message msg) {

                                 }
                            };
                            // Remove item from list
                            finalPersonalizedList.remove(food);
                            user.setPersonalized_List(finalPersonalizedList);
                            UserRepository newRepo = new UserRepository();
                            newRepo.updatePersonalizedList(Executors.newSingleThreadExecutor(), handler, finalPersonalizedList, user.getId());

                            // Notify adapter
                            if (foodAdapter != null) {
                                foodAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    binding.foodItemsRecyclerView.setAdapter(foodAdapter);
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error parsing user data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show();
        }




    }


    // Add a method to format the date
    public String dateFormatter(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM, EEEE", Locale.ENGLISH);
        return LocalDate.now().format(formatter);
    }

    // Add a method to calculate the calorie goal
    public double calculateCalorieGoal(Sex gender, double weight, double height, int age) {
        int Height = (int) (height * 100); // Convert height to centimeters
        double bmr;
        if (gender != null) { // Check if gender is not null
            if ("MALE".equals(gender.toString())) {
                bmr = 10 * weight + 6.25 * Height - 5 * age + 5;
            } else {
                bmr = 10 * weight + 6.25 * Height - 5 * age - 161;
            }
            // Now you can set the calorie goal based on the BMR
            // For example, you can multiply the BMR by an activity factor
            // to get the total daily calorie expenditure goal.

            // For example, if the user's activity level is moderate, you can use a factor of 1.55
            double calorieGoal = bmr * 1.55;

            return calorieGoal;
        } else {
            // Handle the case when gender is null
            return 0.0; // Or throw an exception, depending on your requirements
        }
    }

}
