package com.grievancesystem.speakout.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.databinding.ActivityMessBinding;
import com.grievancesystem.speakout.utility.Helper;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MessActivity extends AppCompatActivity {
    ActivityMessBinding messBinding;

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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messBinding=ActivityMessBinding.inflate(getLayoutInflater());
        setContentView(messBinding.getRoot());

        setSupportActionBar(messBinding.toolbar1);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        messBinding.qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessActivity.this,QRCodeActivity.class));
            }
        });

        messBinding.card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MessActivity.this,SelectMessActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);
            }
        });

        messBinding.card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessActivity.this,MessAttendanceReportActivity.class));
            }
        });

        messBinding.card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MessActivity.this,SelectMessActivity.class);
                intent.putExtra("type",2);
                startActivity(intent);
            }
        });
    }


}