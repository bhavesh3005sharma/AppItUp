package com.grievancesystem.speakout.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.fragments.AboutUsFragment;
import com.grievancesystem.speakout.fragments.AllUsersFragment;
import com.grievancesystem.speakout.fragments.FAQorHelpFragment;
import com.grievancesystem.speakout.fragments.HomeFragment;
import com.grievancesystem.speakout.fragments.LinksFragment;
import com.grievancesystem.speakout.fragments.TrendingFragment;
import com.grievancesystem.speakout.fragments.UserAccountFragment;
import com.grievancesystem.speakout.fragments.YourComplaintsFragment;
import com.grievancesystem.speakout.models.User;
import com.grievancesystem.speakout.utility.Helper;

import java.util.HashMap;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FragmentTransaction transaction;
    ImageView drawerOpener, notifications, app_icon;
    TextView title;
    AppBarLayout appBarLayout;
    DrawerLayout drawerLayout;
    NavigationView navigationDrawer;
    Toolbar toolbar;
    boolean isConnected = true;
    boolean monitoringConnectivity = false;
    View parentLayout;
    AlertDialog alertDialogProgress;
    ExtendedFloatingActionButton fab;
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
    private AppBarConfiguration mAppBarConfiguration;

    private void showBackOnlineUI() {
        Snackbar snackbar = Snackbar.make(parentLayout, "Back Online", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(android.R.color.holo_green_light))
                .setTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    private void showNoInternetUI() {
        Snackbar snackbar = Snackbar.make(parentLayout, "No Internet Connection Available", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(android.R.color.black))
                .setTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(android.R.id.content);
        hideMenu();

        drawerLayout = findViewById(R.id.drawer_layout);
        appBarLayout = findViewById(R.id.app_bar_layout);
        navigationDrawer = findViewById(R.id.nav_view);
        notifications = findViewById(R.id.notifications);
        drawerOpener = findViewById(R.id.drawerOpener);
        title = findViewById(R.id.title);
        app_icon = findViewById(R.id.app_icon);
        fab=findViewById(R.id.fab);

        setToolbarUI(1);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register_Complaint.class));
            }
        });

        drawerOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigationDrawer.setNavigationItemSelectedListener(this);
        navigationDrawer.setItemIconTintList(null);
        transaction = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null)
            openFragment(new HomeFragment());

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
            }
        });
    }

    public void hideMenu() {
        navigationDrawer = findViewById(R.id.nav_view);
        Menu nav_menu = navigationDrawer.getMenu();
        if (Prefs.getUser(MainActivity.this).getUserType() == Helper.USER_STUDENT)
            nav_menu.findItem(R.id.nav_all_user).setVisible(false);
        if (Prefs.getUser(MainActivity.this).getUserType() == Helper.USER_ADMINISTRATOR)
            nav_menu.findItem(R.id.nav_your_complaints).setVisible(false);
    }

    private void setToolbarUI(int i) {
        Typeface faceDashboard = Typeface.createFromAsset(getAssets(),
                "font/lobster_two.ttf");
        Typeface faceOthers = Typeface.createFromAsset(getAssets(),
                "font/rubik_light.ttf");
        if (i == 1) {
            title.setTypeface(faceDashboard);
            title.setTextSize(30f);
            app_icon.setVisibility(View.VISIBLE);
        } else {
            title.setTypeface(faceOthers);
            app_icon.setVisibility(View.GONE);
            title.setTextSize(25f);
        }
    }

    public void openFragment(Fragment fragment) {
        new Timer().cancel();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();

    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                fab.setVisibility(View.VISIBLE);
                openFragment(new HomeFragment());
                title.setText(getString(R.string.app_name));
                setToolbarUI(1);
                closeDrawer();
                break;
            case R.id.nav_notices:
                startActivity(new Intent(this, NoticeActivity.class));
                closeDrawer();
                break;
            case R.id.nav_trending:
                fab.setVisibility(View.VISIBLE);
                openFragment(new TrendingFragment());
                title.setText(getString(R.string.trending));
                setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.nav_user_account:
                fab.setVisibility(View.VISIBLE);
                openFragment(new UserAccountFragment());
                title.setText(getString(R.string.user_account));
                setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.nav_all_user:
                openFragment(new AllUsersFragment());
                title.setText(getString(R.string.all_users));
                setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.nav_your_complaints:
                fab.setVisibility(View.VISIBLE);
                openFragment(new YourComplaintsFragment());
                title.setText(getString(R.string.your_complaints));
                setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.campus_menu:
                startActivity(new Intent(MainActivity.this, MapsCampus.class));
                //title.setText("Campus Menu");
                //setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.nav_about_us:
                fab.setVisibility(View.GONE);
                openFragment(new AboutUsFragment());
                title.setText(getString(R.string.about_us));
                setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.nav_faq_help:
                fab.setVisibility(View.GONE);
                openFragment(new FAQorHelpFragment());
                title.setText(getString(R.string.faq_help));
                setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.mis:
                fab.setVisibility(View.GONE);
                Bundle bundle = new Bundle();
                bundle.putString("url", "https://mis.iitism.ac.in/");
                LinksFragment fragobj = new LinksFragment();
                fragobj.setArguments(bundle);
                openFragment(fragobj);
                title.setText("MIS");
                setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.parentPortal:
                fab.setVisibility(View.GONE);
                Bundle bundle1 = new Bundle();
                bundle1.putString("url", "https://parent.iitism.ac.in/index.php/parent_portal/portal0");
                LinksFragment fragobj1 = new LinksFragment();
                fragobj1.setArguments(bundle1);
                openFragment(fragobj1);
                title.setText("Parent Portal");
                setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.md:
                fab.setVisibility(View.GONE);
                Bundle bundle2 = new Bundle();
                bundle2.putString("url", "https://www.facebook.com/MDiitism/");
                LinksFragment fragobj2 = new LinksFragment();
                fragobj2.setArguments(bundle2);
                openFragment(fragobj2);
                title.setText("Mailer Daemon");
                setToolbarUI(2);
                closeDrawer();
                break;
            case R.id.action_sign_out:
                closeDrawer();
                showProgressDialogue();
                signOut();
                break;
        }
        return true;
    }

    private void signOut() {
        User user = Prefs.getUser(this);
        DatabaseReference databaseReference;
        if (user.getUserType() == Helper.USER_STUDENT)
            databaseReference = FirebaseDatabase.getInstance().getReference("StudentUsers");
        else databaseReference = FirebaseDatabase.getInstance().getReference("AdminUsers");

        HashMap<String, Object> data = new HashMap<>();
        data.put("fcm_token", null);
        data.put("isLoggedIn", false);
        databaseReference.child(user.getUid()).updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                alertDialogProgress.dismiss();
                if (task.isSuccessful()) {
                    Helper.toast(MainActivity.this, "Sign Out Successful");
                    Helper.signOutUser(MainActivity.this, true);
                    finish();
                } else {
                    // "Failed to log out!!\n"+task.getException().getMessage()
                    Helper.toast(MainActivity.this, task.getException().getMessage());
                }
            }
        });
    }

    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogue_loading, null);
        builder.setView(v);
        TextView title = v.findViewById(R.id.textViewTitle);
        v.findViewById(R.id.progressLoader).setVisibility(View.VISIBLE);

        title.setText("Please wait we are logging you out...");

        alertDialogProgress = builder.create();
        alertDialogProgress.setCancelable(false);
        alertDialogProgress.show();
    }
}