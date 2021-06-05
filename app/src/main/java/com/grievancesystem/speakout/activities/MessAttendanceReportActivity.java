package com.grievancesystem.speakout.activities;

import android.os.Bundle;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grievancesystem.speakout.adapter.DietAdapter;
import com.grievancesystem.speakout.databinding.ActivityMessAttendanceReportBinding;
import com.grievancesystem.speakout.models.DayFoodDietStat;
import com.grievancesystem.speakout.models.FoodDietStatistic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MessAttendanceReportActivity extends AppCompatActivity {

    ActivityMessAttendanceReportBinding activityMessAttendanceReportBinding;
    DietAdapter weekDietAdapter, monthDietAdapter;
    ArrayList<FoodDietStatistic> weekDietList = new ArrayList<>(), monthDietList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityMessAttendanceReportBinding = ActivityMessAttendanceReportBinding.inflate(getLayoutInflater());
        setContentView(activityMessAttendanceReportBinding.getRoot());

        // set Toolbar
        getSupportActionBar().setTitle("Mess Attendance Record");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initCalender();
        initWeekDietRecyclerView();
        initMonthDietRecyclerView();

        loadWeekData();
        loadMonthData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void loadMonthData() {
        monthDietList.clear();
        for (int i = 0; i < 12; i++) {
            FoodDietStatistic dietStatistic = new FoodDietStatistic(getRandomValue(20, 100), getRandomValue(20, 100), getRandomValue(20, 100));
            switch (i) {
                case 0:
                    dietStatistic.setTitle("Record of January : ");
                    break;
                case 1:
                    dietStatistic.setTitle("Record of February : ");
                    break;
                case 2:
                    dietStatistic.setTitle("Record of March : ");
                    break;
                case 3:
                    dietStatistic.setTitle("Record of April : ");
                    break;
                case 4:
                    dietStatistic.setTitle("Record of May : ");
                    break;
                case 5:
                    dietStatistic.setTitle("Record of June : ");
                    break;
                case 6:
                    dietStatistic.setTitle("Record of July : ");
                    break;
                case 7:
                    dietStatistic.setTitle("Record of August : ");
                    break;
                case 8:
                    dietStatistic.setTitle("Record of September : ");
                    break;
                case 9:
                    dietStatistic.setTitle("Record of October : ");
                    break;
                case 10:
                    dietStatistic.setTitle("Record of November : ");
                    break;
                case 11:
                    dietStatistic.setTitle("Record of December : ");
                    break;
            }
            monthDietList.add(dietStatistic);
        }
        monthDietAdapter.notifyDataSetChanged();
    }

    private void loadWeekData() {
        weekDietList.clear();
        for (int i = 0; i < 7; i++) {
            FoodDietStatistic dietStatistic = new FoodDietStatistic(getRandomValue(20, 100), getRandomValue(20, 100), getRandomValue(20, 100));
            switch (i) {
                case 0:
                    dietStatistic.setTitle("Record of Sunday : ");
                    break;
                case 1:
                    dietStatistic.setTitle("Record of Monday : ");
                    break;
                case 2:
                    dietStatistic.setTitle("Record of Tuesday : ");
                    break;
                case 3:
                    dietStatistic.setTitle("Record of Wednesday : ");
                    break;
                case 4:
                    dietStatistic.setTitle("Record of Thursday : ");
                    break;
                case 5:
                    dietStatistic.setTitle("Record of Friday : ");
                    break;
                case 6:
                    dietStatistic.setTitle("Record of Saturday : ");
                    break;
            }
            weekDietList.add(dietStatistic);
        }
        weekDietAdapter.notifyDataSetChanged();
    }

    private void initWeekDietRecyclerView() {
        weekDietAdapter = new DietAdapter(weekDietList, MessAttendanceReportActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        activityMessAttendanceReportBinding.weeklyRecordRecycler.setLayoutManager(linearLayoutManager);
        activityMessAttendanceReportBinding.weeklyRecordRecycler.setAdapter(weekDietAdapter);
        activityMessAttendanceReportBinding.weeklyRecordRecycler.setHasFixedSize(true);
    }

    private void initMonthDietRecyclerView() {
        monthDietAdapter = new DietAdapter(monthDietList, MessAttendanceReportActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        activityMessAttendanceReportBinding.monthlyRecordRecycler.setLayoutManager(linearLayoutManager);
        activityMessAttendanceReportBinding.monthlyRecordRecycler.setAdapter(monthDietAdapter);
        activityMessAttendanceReportBinding.monthlyRecordRecycler.setHasFixedSize(true);
    }


    private void initCalender() {
        Calendar calendar = Calendar.getInstance();
        activityMessAttendanceReportBinding.calendarView.setMaxDate(calendar.getTimeInMillis());
        Date date = new Date(calendar.getTimeInMillis());
        calendar.add(Calendar.YEAR, -1);
        activityMessAttendanceReportBinding.calendarView.setMinDate(calendar.getTimeInMillis());

        DayFoodDietStat diet = new DayFoodDietStat(
                "Today's Record :",
                getRandomValue(-1, 1), getRandomValue(-1, 1), getRandomValue(-1, 1));
        activityMessAttendanceReportBinding.dateWiseRecord.setDiet(diet);

        activityMessAttendanceReportBinding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                DayFoodDietStat diet = new DayFoodDietStat(
                        "Record of " + day + "/" + (month + 1) + "/" + year + " :",
                        getRandomValue(-1, 1), getRandomValue(-1, 1), getRandomValue(-1, 1));
                activityMessAttendanceReportBinding.dateWiseRecord.setDiet(diet);
            }
        });
    }

    public int getRandomValue(int mn, int mx) {
        Random r = new Random();
        return r.nextInt((mx - mn) + 1) + mn;
    }

}