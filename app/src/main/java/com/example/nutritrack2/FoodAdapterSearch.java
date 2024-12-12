package com.example.nutritrack2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapterSearch extends RecyclerView.Adapter<FoodAdapterSearch.FoodViewHolder> {

    private final List<Food> foodList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Food food);
    }

    public FoodAdapterSearch(List<Food> foodList, OnItemClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.foodName.setText(food.getName());
        holder.foodCalories.setText(String.format("%.2f calories", food.getFoodWeight()));
        holder.itemView.setOnClickListener(view -> listener.onItemClick(food));
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView foodCalories;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.food_name);
            foodCalories = itemView.findViewById(R.id.food_calories);
        }
    }
}
