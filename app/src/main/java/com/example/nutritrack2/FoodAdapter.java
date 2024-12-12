package com.example.nutritrack2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutritrack2.Food;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private List<Food> foodList;
    private OnItemClickListener listener;

    public FoodAdapter(List<Food> foodList, OnItemClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.bind(food, listener);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView foodNameTextView;
        private Button removeButton; // Add remove button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodNameTextView = itemView.findViewById(R.id.food_name_text_view);
            removeButton = itemView.findViewById(R.id.buttonRemove); // Initialize remove button
        }

        public void bind(final Food food, final OnItemClickListener listener) {
            foodNameTextView.setText(food.getName());

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(food);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Food food);
    }
}
