package com.grievancesystem.speakout.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grievancesystem.speakout.databinding.FoodDietStatisticBinding;
import com.grievancesystem.speakout.models.FoodDietStatistic;

import java.util.ArrayList;

public class DietAdapter extends RecyclerView.Adapter<DietAdapter.viewHolder> {

    ArrayList<FoodDietStatistic> list;
    Context context;

    public DietAdapter(ArrayList<FoodDietStatistic> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FoodDietStatisticBinding foodDietStatisticBinding = FoodDietStatisticBinding.inflate(layoutInflater, parent, false);
        return new viewHolder(foodDietStatisticBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        FoodDietStatistic dietStatistic = list.get(position);
        holder.foodDietStatisticBinding.setDiet(dietStatistic);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        FoodDietStatisticBinding foodDietStatisticBinding;

        @SuppressLint("ClickableViewAccessibility")
        public viewHolder(@NonNull FoodDietStatisticBinding foodDietStatisticBinding) {
            super(foodDietStatisticBinding.getRoot());
            this.foodDietStatisticBinding = foodDietStatisticBinding;
            View.OnTouchListener listener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            };
            foodDietStatisticBinding.breakfastProgress.setOnTouchListener(listener);
            foodDietStatisticBinding.lunchProgress.setOnTouchListener(listener);
            foodDietStatisticBinding.dinnerProgress.setOnTouchListener(listener);
        }
    }
}
