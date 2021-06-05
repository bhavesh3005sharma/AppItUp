package com.grievancesystem.speakout.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.chip.ChipGroup;
import com.grievancesystem.speakout.databinding.ActivitySelectMessBinding;
import com.grievancesystem.speakout.models.Inventory;

public class SelectMessActivity extends AppCompatActivity {
    ActivitySelectMessBinding selectMessBinding;
    int type=-1,checkMess=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectMessBinding=ActivitySelectMessBinding.inflate(getLayoutInflater());
        setContentView(selectMessBinding.getRoot());

        getSupportActionBar().setTitle("Select Mess");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent=getIntent();
        type=intent.getIntExtra("type",-1);

        selectMessBinding.mess.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if(type==1){
                    Intent intent1=new Intent(SelectMessActivity.this, MessInventoryActivity.class);
                    intent1.putExtra("hostel",checkedId);
                    startActivity(intent1);
                }
                else{
                    selectMessBinding.days.setVisibility(View.VISIBLE);
                    checkMess=checkedId;
                }
            }
        });
        selectMessBinding.days.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Intent intent2=new Intent(SelectMessActivity.this, MessScheduleActivity.class);
                intent2.putExtra("hostel",checkMess);
                intent2.putExtra("day",checkedId);
                startActivity(intent2);
            }
        });
    }
}