package com.grievancesystem.speakout.models;

public class DayFoodDietStat {
    String title;

    /*
        Diet has following values to track :
        public static int TAKEN = 1;
        public static int PENDING = 0;
        public static int NOT_TAKEN = -1;
     */
    int isBreakFastTaken;

    int isLunchTaken;

    int isDinnerTaken;

    public DayFoodDietStat(String title, int isBreakFastTaken, int isLunchTaken, int isDinnerTaken) {
        this.title = title;
        this.isBreakFastTaken = isBreakFastTaken;
        this.isLunchTaken = isLunchTaken;
        this.isDinnerTaken = isDinnerTaken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsBreakFastTaken() {
        return isBreakFastTaken;
    }

    public void setIsBreakFastTaken(int isBreakFastTaken) {
        this.isBreakFastTaken = isBreakFastTaken;
    }

    public int getIsLunchTaken() {
        return isLunchTaken;
    }

    public void setIsLunchTaken(int isLunchTaken) {
        this.isLunchTaken = isLunchTaken;
    }

    public int getIsDinnerTaken() {
        return isDinnerTaken;
    }

    public void setIsDinnerTaken(int isDinnerTaken) {
        this.isDinnerTaken = isDinnerTaken;
    }
}
