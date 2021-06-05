package com.grievancesystem.speakout.models;

public class FoodDietStatistic {
    String title;

    int breakfastProgress;

    int lunchProgress;

    int dinnerProgress;

    public FoodDietStatistic(int breakfastProgress, int lunchProgress, int dinnerProgress) {
        this.breakfastProgress = breakfastProgress;
        this.lunchProgress = lunchProgress;
        this.dinnerProgress = dinnerProgress;
    }

    public FoodDietStatistic(String title, int breakfastProgress, int lunchProgress, int dinnerProgress) {
        this.title = title;
        this.breakfastProgress = breakfastProgress;
        this.lunchProgress = lunchProgress;
        this.dinnerProgress = dinnerProgress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBreakfastProgress() {
        return breakfastProgress;
    }

    public void setBreakfastProgress(int breakfastProgress) {
        this.breakfastProgress = breakfastProgress;
    }

    public int getLunchProgress() {
        return lunchProgress;
    }

    public void setLunchProgress(int lunchProgress) {
        this.lunchProgress = lunchProgress;
    }

    public int getDinnerProgress() {
        return dinnerProgress;
    }

    public void setDinnerProgress(int dinnerProgress) {
        this.dinnerProgress = dinnerProgress;
    }
}
