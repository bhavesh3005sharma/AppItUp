package com.grievancesystem.speakout.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.grievancesystem.speakout.databinding.ActivityMessScheduleBinding;

public class MessScheduleActivity extends AppCompatActivity {
ActivityMessScheduleBinding messScheduleBinding;
int hostel=-1,day=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messScheduleBinding=ActivityMessScheduleBinding.inflate(getLayoutInflater());
        setContentView(messScheduleBinding.getRoot());

        Intent intent=getIntent();
        hostel=intent.getIntExtra("hostel",-1);
        day=intent.getIntExtra("day",-1);

        getSupportActionBar().setTitle("Mess Schedule");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}