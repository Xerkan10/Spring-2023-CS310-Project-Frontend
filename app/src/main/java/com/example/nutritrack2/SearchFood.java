package com.example.nutritrack2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.Gson;

import java.util.concurrent.Executors;

public class SearchFood extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodAdapterSearch foodAdapter;
    private List<Food> foodList;
    private List<Food> filteredFoodList;
    private UserRepository repoForPersonalizedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        recyclerView = findViewById(R.id.recycler_view);
        EditText searchBar = findViewById(R.id.search_bar);
        Button btnBackHome = findViewById(R.id.btn_back_home);

        filteredFoodList = new ArrayList<>(); // Initialize filteredFoodList

        Handler uiHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                foodList = (List<Food>) msg.obj;
                filteredFoodList.addAll(foodList); // Initialize filteredFoodList with all food items
                foodAdapter = new FoodAdapterSearch(filteredFoodList, SearchFood.this::onFoodItemClick);
                recyclerView.setAdapter(foodAdapter);

                // Search bar text change listener
                searchBar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        filterFoods(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });

                // Back to home button click listener
                btnBackHome.setOnClickListener(view -> {
                    finish();
                });
            }
        };


        UserRepository userRepo = new UserRepository();
        userRepo.getAllFood(Executors.newSingleThreadExecutor(), uiHandler);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void filterFoods(String query) {
        filteredFoodList.clear();
        if (query.isEmpty()) {
            filteredFoodList.addAll(foodList);
        } else {
            filteredFoodList.addAll(foodList.stream()
                    .filter(food -> food.getName().toLowerCase().startsWith(query.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        foodAdapter.notifyDataSetChanged();
    }

    private void onFoodItemClick(Food food) {

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };

        Intent intent = new Intent(SearchFood.this, HomePage.class);
        String userJson = getIntent().getStringExtra("user");
        User user = User.fromJson(userJson);
        user.addFoodToPersonalizedList(food);
        List<Food> temp = user.getPersonalized_List();
        for (Food a : temp){
            System.err.println("food name:" + a.getName());
        }

        UserRepository newRepo = new UserRepository();
        newRepo.updatePersonalizedList(Executors.newSingleThreadExecutor(), handler, user.getPersonalized_List(), user.getId());

        //updatePersonalizedList()
        intent.putExtra("selected_food_name", food.getName());
        intent.putExtra("user_search", new Gson().toJson(user));
        startActivity(intent);
    }
}