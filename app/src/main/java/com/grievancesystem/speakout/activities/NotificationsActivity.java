package com.grievancesystem.speakout.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.adapter.NotificationsAdapter;
import com.grievancesystem.speakout.models.Notification;
import com.grievancesystem.speakout.utility.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotificationsActivity extends AppCompatActivity implements NotificationsAdapter.NotificationsListener {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.shimmer3)
    ShimmerFrameLayout shimmerFrameLayout;

    Unbinder unbinder;
    ArrayList<Notification> list = new ArrayList<>();
    NotificationsAdapter adapter;

    boolean isConnected = true;
    boolean monitoringConnectivity = false;
    View parentLayout;
    private final ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            showBackOnlineUI();
            isConnected = true;
        }

        @Override
        public void onLost(Network network) {
            showNoInternetUI();
            isConnected = false;
        }
    };
    String string = null;

    private void showBackOnlineUI() {
        if (parentLayout != null)
            Snackbar.make(parentLayout, "Back Online", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(android.R.color.holo_green_light))
                    .setTextColor(getResources().getColor(android.R.color.white)).show();
    }

    private void showNoInternetUI() {
        if (parentLayout != null)
            Snackbar.make(parentLayout, "No Internet Connection Available", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(android.R.color.black))
                    .setTextColor(getResources().getColor(android.R.color.white)).show();
    }

    @Override
    protected void onPause() {
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }

    }

    @Override
    protected void onStart() {
        if (!Helper.isInternetAvailable(this)) {
            showNoInternetUI();
        }
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        parentLayout = findViewById(android.R.id.content);

        unbinder = ButterKnife.bind(this);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        adapter = new NotificationsAdapter(getApplicationContext(), list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setUpOnNotificationListener(this);

        loadData();
    }

    public void loadData() {
        list.clear();
        DatabaseReference query = FirebaseDatabase.getInstance().getReference("Notifications").child(Prefs.getUser(getApplicationContext()).getUsername());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = "blah";
                    if (ds.getKey() != null) key = ds.getKey();
                    String title = String.valueOf(ds.child("title").getValue());
                    //if(key!=null)string+=" "+key ;
                    String message = String.valueOf(ds.child("message").getValue());
                    String complaint_id = null;
                    if (ds.child("complaint_id").exists()) {
                        complaint_id = String.valueOf(ds.child("complaint_id").getValue());
                    }
                    long timeStamp = 0;
                    Map<String, Long> map = new HashMap();
                    if (ds.child("timeStampMap").child("timeStamp").exists()) {
                        timeStamp = (long) ds.child("timeStampMap").child("timeStamp").getValue();
                        map.put("timeStamp", timeStamp);
                    }
                    list.add(new Notification(title, message, complaint_id, map));
                }
                if (list.isEmpty()) {
                    Helper.toast(NotificationsActivity.this, "No notifications");
                }
                if (adapter != null && shimmerFrameLayout != null) {
                    adapter.notifyDataSetChanged();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void chipClicked(Notification notification) {
        String complaintId = notification.getComplaint_id();
        Intent intent = new Intent(NotificationsActivity.this, ComplaintActivity.class);
        intent.putExtra("complaintId", complaintId);
        startActivity(intent);

    }
}