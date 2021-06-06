package com.grievancesystem.speakout.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.adapter.NoticeAdapter;
import com.grievancesystem.speakout.databinding.ActivityNoticeBinding;
import com.grievancesystem.speakout.databinding.DialogueCreateNoticeBinding;
import com.grievancesystem.speakout.models.Notice;
import com.grievancesystem.speakout.utility.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoticeActivity extends AppCompatActivity {
    ActivityNoticeBinding activityNoticeBinding;
    NoticeAdapter adapter;
    ArrayList<Notice> list = new ArrayList<>();
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view binding
        activityNoticeBinding = ActivityNoticeBinding.inflate(getLayoutInflater());
        setContentView(activityNoticeBinding.getRoot());

        // set Toolbar
        getSupportActionBar().setTitle("Notice Board");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initRecyclerView();
        loadData();

        if(Prefs.getUser(this).getUserType()==Helper.USER_STUDENT){
            activityNoticeBinding.fabCreateNewNotice.setVisibility(View.GONE);
        }

        activityNoticeBinding.fabCreateNewNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNotice();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void createNotice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NoticeActivity.this);
        DialogueCreateNoticeBinding dialogueCreateNoticeBinding = DialogueCreateNoticeBinding.inflate(getLayoutInflater());
        builder.setView(dialogueCreateNoticeBinding.getRoot());

        alertDialog = builder.create();
        alertDialog.show();

        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(null);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

        dialogueCreateNoticeBinding.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = dialogueCreateNoticeBinding.textInputTitle.getEditText().getText().toString().trim();
                String message = dialogueCreateNoticeBinding.textInputMessage.getEditText().getText().toString().trim();

                if (title.isEmpty()) {
                    dialogueCreateNoticeBinding.textInputTitle.setError("Title is Required");
                    dialogueCreateNoticeBinding.textInputTitle.requestFocus();
                    return;
                } else dialogueCreateNoticeBinding.textInputTitle.setError(null);

                if (message.isEmpty()) {
                    dialogueCreateNoticeBinding.textInputMessage.setError("Message is Required");
                    dialogueCreateNoticeBinding.textInputMessage.requestFocus();
                    return;
                } else dialogueCreateNoticeBinding.textInputMessage.setError(null);

                Map map = new HashMap();
                map.put("timeStamp", ServerValue.TIMESTAMP);
                Notice notice = new Notice(title, message, map, Prefs.getUser(NoticeActivity.this).getUsername());

                dialogueCreateNoticeBinding.textInputTitle.setEnabled(false);
                dialogueCreateNoticeBinding.textInputMessage.setEnabled(false);
                dialogueCreateNoticeBinding.submitButton.setEnabled(false);
                dialogueCreateNoticeBinding.progressLoader.setVisibility(View.VISIBLE);

                publishNotice(notice);
                builder.setCancelable(false);
            }
        });

        /*
        Notice notice = new Notice("Title ", "Message", new HashMap(), "bhavesh");
        notice.setPhotoUrl("https://i.ytimg.com/vi/mDsxgxfpYRA/maxresdefault.jpg");
        list.add(notice);
        adapter.notifyDataSetChanged();
         */
    }

    private void publishNotice(Notice notice) {
        FirebaseDatabase.getInstance().getReference("Notices").push().setValue(notice).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                alertDialog.dismiss();
                if (task.isSuccessful()) {
                    Helper.toast(NoticeActivity.this, "Notice added Successfully");
                } else Helper.toast(NoticeActivity.this, task.getException().getMessage());
            }
        });
    }

    private void loadData() {
        activityNoticeBinding.shimmer.startShimmer();
        activityNoticeBinding.shimmer.setVisibility(View.VISIBLE);
        DatabaseReference query = FirebaseDatabase.getInstance().getReference("Notices");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String title = String.valueOf(ds.child("title").getValue());
                    String message = String.valueOf(ds.child("message").getValue());
                    String issuedBy = String.valueOf(ds.child("issuedBy").getValue());
                    String photoUrl = String.valueOf(ds.child("photoUrl").getValue());
                    long timeStamp = 0;
                    Map<String, Long> map = new HashMap();
                    if (ds.child("timeStampMap").child("timeStamp").exists()) {
                        timeStamp = (long) ds.child("timeStampMap").child("timeStamp").getValue();
                        map.put("timeStamp", timeStamp);
                    }
                    list.add(new Notice(title, message, map, issuedBy, photoUrl));
                }
                if (list.isEmpty()) {
                    Helper.toast(NoticeActivity.this, "No notifications");
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    activityNoticeBinding.shimmer.stopShimmer();
                    activityNoticeBinding.shimmer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                activityNoticeBinding.shimmer.stopShimmer();
                activityNoticeBinding.shimmer.setVisibility(View.GONE);
                Helper.toast(NoticeActivity.this, error.getMessage());
            }
        });
    }

    private void initRecyclerView() {
        adapter = new NoticeAdapter(list, NoticeActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        activityNoticeBinding.recyclerViewNotices.setLayoutManager(linearLayoutManager);
        activityNoticeBinding.recyclerViewNotices.setAdapter(adapter);
        activityNoticeBinding.recyclerViewNotices.setHasFixedSize(true);
    }
}