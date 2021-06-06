package com.grievancesystem.speakout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.models.User;
import com.grievancesystem.speakout.utility.Helper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                takeAction();
            }
        }, 2000);

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    private void takeAction() {
        Intent intent;
        if (Prefs.isUserLoggedIn(SplashActivity.this)) {
            //checkIsBlocked();
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, SignIn.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        subscribeDeviceToTopic();
    }

    private void subscribeDeviceToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "msg_subscribed";
                        if (!task.isSuccessful()) {
                            msg = "msg_subscribe_failed " + task.getException().getMessage();
                        }
                        Log.d("TAG", msg);
                    }
                });
    }

    private void checkIsBlocked() {
        User user = Prefs.getUser(SplashActivity.this);
        Query query;
        if (user.getUserType() == Helper.USER_STUDENT)
            query = FirebaseDatabase.getInstance().getReference("StudentUsers");
        else query = FirebaseDatabase.getInstance().getReference("AdminUsers");

        query.orderByChild("username").equalTo(user.getUsername())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                User user = getUserModelFromDS(ds);
                                if (user.isBlocked()) {
                                    Helper.toast(SplashActivity.this, "You have been blocked by your college admin.");
                                    Helper.signOutUser(SplashActivity.this, true);
                                } else {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private User getUserModelFromDS(DataSnapshot ds) {
        User user = new User((String) ds.child("username").getValue(), (String) ds.child("email").getValue(),
                (String) ds.child("displayName").getValue(), (String) ds.child("profileUri").getValue(), (String) ds.child("uid").getValue());
        if (ds.hasChild("isBlocked") && ds.child("isBlocked").getValue().equals(true))
            user.setBlocked(true);
        return user;
    }
}
