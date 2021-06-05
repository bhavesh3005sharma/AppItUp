package com.grievancesystem.speakout.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.database.ServerValue;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.adapter.InventoryAdapter;
import com.grievancesystem.speakout.adapter.NoticeAdapter;
import com.grievancesystem.speakout.databinding.ActivityMessInventoryBinding;
import com.grievancesystem.speakout.databinding.DialogueCreateNoticeBinding;
import com.grievancesystem.speakout.models.Inventory;
import com.grievancesystem.speakout.models.Notice;
import com.grievancesystem.speakout.utility.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessInventoryActivity extends AppCompatActivity implements InventoryAdapter.InventoryListener {
    ActivityMessInventoryBinding inventoryBinding;
    ArrayList<Inventory> inventoryArrayList=new ArrayList<>();
    InventoryAdapter adapter;
    AlertDialog alertDialog;
    int hostel=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inventoryBinding=ActivityMessInventoryBinding.inflate(getLayoutInflater());
        setContentView(inventoryBinding.getRoot());

        Intent intent=getIntent();
        hostel=intent.getIntExtra("hostel",-1);

        getSupportActionBar().setTitle("Inventory");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initRecyclerView();
        loadData();

        if (Prefs.getUser(this).getUserType()== Helper.USER_STUDENT){
            inventoryBinding.fabCreateNewItem.setVisibility(View.GONE);
        }

        inventoryBinding.fabCreateNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createItem();
            }
        });

        adapter.setUpOnInventoryListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void createItem(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MessInventoryActivity.this);
        DialogueCreateNoticeBinding dialogueCreateNoticeBinding = DialogueCreateNoticeBinding.inflate(getLayoutInflater());
        builder.setView(dialogueCreateNoticeBinding.getRoot());

        alertDialog = builder.create();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(null);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.show();

        dialogueCreateNoticeBinding.textViewTitle.setText("Create a new Item");
        dialogueCreateNoticeBinding.textInputTitle.setHint("Enter Item Name");
        dialogueCreateNoticeBinding.textInputMessage.setHint("Enter quantity");

        dialogueCreateNoticeBinding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = dialogueCreateNoticeBinding.textInputTitle.getEditText().getText().toString().trim();
                String message = dialogueCreateNoticeBinding.textInputMessage.getEditText().getText().toString().trim();

                if (title.isEmpty()) {
                    dialogueCreateNoticeBinding.textInputTitle.setError("Name is Required");
                    dialogueCreateNoticeBinding.textInputTitle.requestFocus();
                    return;
                } else dialogueCreateNoticeBinding.textInputTitle.setError(null);

                if (message.isEmpty()) {
                    dialogueCreateNoticeBinding.textInputMessage.setError("Quantity is Required");
                    dialogueCreateNoticeBinding.textInputMessage.requestFocus();
                    return;
                } else dialogueCreateNoticeBinding.textInputMessage.setError(null);

                Inventory inventory = new Inventory(title, message);

                dialogueCreateNoticeBinding.textInputTitle.setEnabled(false);
                dialogueCreateNoticeBinding.textInputMessage.setEnabled(false);
                dialogueCreateNoticeBinding.submitButton.setEnabled(false);
                dialogueCreateNoticeBinding.progressLoader.setVisibility(View.VISIBLE);

                //publishNotice(notice);
                builder.setCancelable(false);
            }
        });

    }

    @Override
    public void editClicked(Inventory inventory, int position) {

    }

    @Override
    public void saveClicked(Inventory inventory, int position) {

    }

    @Override
    public void deleteClicked(Inventory inventory, int position) {

    }
    private void initRecyclerView(){
        adapter=new InventoryAdapter(MessInventoryActivity.this,inventoryArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        //linearLayoutManager.setReverseLayout(true);
        inventoryBinding.recyclerViewInventory.setLayoutManager(linearLayoutManager);
        inventoryBinding.recyclerViewInventory.setAdapter(adapter);
        inventoryBinding.recyclerViewInventory.setHasFixedSize(true);
    }
    private void loadData(){
        String items[]=new String[]{"Tomatoes","Onions","Potatoes","Cucumber","Rice","Basmati Rice","Beetroot","Wheat","Milk","Biscuits","Tea Powder"};
        String quantity[]=new String[]{"100 kg","50 kg","70 kg","15 kg","150 kg","100 kg","20 kg","100 kg","25 l","50 packets","10 kg"};
        for(int i=0;i<11;i++){
            inventoryArrayList.add(new Inventory(items[i],quantity[i]));
        }
        if (adapter != null)adapter.notifyDataSetChanged();
    }
}