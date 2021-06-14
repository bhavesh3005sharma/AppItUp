package com.grievancesystem.speakout.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.adapter.MapAdapter;
import com.grievancesystem.speakout.models.MapPlaces;
import com.grievancesystem.speakout.utility.Helper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MapsCampus extends FragmentActivity implements OnMapReadyCallback,MapAdapter.MapListener {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    private GoogleMap mMap;
    int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private int COLOR_BLACK_ARGB = -0x1000000;
    private int POLYLINE_STROKE_WIDTH_PX = 2;

    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    LocationCallback mLocationCallback;
    Unbinder unbinder;
    MapAdapter adapter;
    ArrayList<MapPlaces> list=new ArrayList<>();
    String[] names;
    ArrayList<LatLng> latLngList=new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_campus);
        unbinder = ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this);

        names=new String[]{"Penman","Health Centre","Ruby","Rosaline","Jasper","Amber","Diamond","NLHC","SAC","RD","Central Library","Academic Block","Central Workshop","Temple","Post Office/ATM","Upper Ground","Lower Ground","Main Canteen"};
        for(int i=0;i<names.length;i++){
            list.add(new MapPlaces(names[i]));
        }
        adapter=new MapAdapter(this,list);
        adapter.setOnPlaceClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        latLngList.add(new LatLng(23.814902, 86.441178));
        latLngList.add(new LatLng(23.811926, 86.439028));
        latLngList.add(new LatLng(23.813257, 86.444626));
        latLngList.add(new LatLng(23.813426, 86.445107));
        latLngList.add(new LatLng(23.817035, 86.440934));
        latLngList.add(new LatLng(23.818534,86.439684));
        latLngList.add(new LatLng(23.815403,86.440387));
        latLngList.add(new LatLng(23.816283,86.439715));
        latLngList.add(new LatLng(23.817462, 86.437456));
        latLngList.add(new LatLng(23.816504,86.440286));
        latLngList.add(new LatLng(23.816304,86.442220));
        latLngList.add(new LatLng(23.811628,86.441711));
        latLngList.add(new LatLng(23.815015,86.439683));
        latLngList.add(new LatLng(23.811583,86.439819));
        latLngList.add(new LatLng(23.811516,86.442283));
        latLngList.add(new LatLng(23.812580,86.440860));
        latLngList.add(new LatLng(23.813120,86.442032));
        latLngList.add(new LatLng(23.815049,86.441593));

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ism = new LatLng(23.814110, 86.441207);
        LatLng ismGate = new LatLng(23.809163, 86.442590);
        LatLng jasper = new LatLng(23.817035, 86.440934);
        LatLng ruby = new LatLng(23.813257, 86.444626);
        mMap.addMarker(new MarkerOptions().position(ism).title("Heritage")).showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(ismGate).title("Main Entrance")).showInfoWindow();
        //mMap.addMarker(new MarkerOptions().position(jasper).title("Jasper")).showInfoWindow();
        //mMap.addMarker(new MarkerOptions().position(ruby).title("Ruby")).showInfoWindow();
        LatLng sac = new LatLng(23.817462, 86.437456);
        mMap.addMarker(new MarkerOptions().position(sac).title("SAC")).showInfoWindow();
        LatLng pm = new LatLng(23.814902, 86.441178);
        mMap.addMarker(new MarkerOptions().position(pm).title("Penman")).showInfoWindow();
        LatLng hc = new LatLng(23.811926, 86.439028);
        mMap.addMarker(new MarkerOptions().position(hc).title("Health Centre")).showInfoWindow();
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(23.821271, 86.435213),
                        new LatLng(23.819827, 86.434614),
                        new LatLng(23.818309, 86.436635),
                        new LatLng(23.817824, 86.436289),
                        new LatLng(23.815959, 86.439142),
                        new LatLng(23.811823, 86.436986),
                        new LatLng(23.810356, 86.437202),
                        new LatLng(23.809208, 86.441401),
                        new LatLng(23.808920, 86.442469),
                        new LatLng(23.811918, 86.444478),
                        new LatLng(23.811966, 86.447407),
                        new LatLng(23.814787, 86.447845),
                        new LatLng(23.816345, 86.442538),
                        new LatLng(23.817181, 86.442852),
                        new LatLng(23.818064, 86.440134),
                        new LatLng(23.819137, 86.440809),
                        new LatLng(23.819931, 86.439832),
                        new LatLng(23.818626, 86.438828),
                        new LatLng(23.821271, 86.435213)
                ));
        stylePolyline(polyline1);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        int width1 = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        int padding = (int)(width1 * 0.12); // offset from edges of the map 12% of screen
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ism));
        LatLngBounds bound = new LatLngBounds(new LatLng(23.809756, 86.433533), new LatLng(23.820778, 86.449679));
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, 20));
            }
        });
        locationRequest();

    }

    public void stylePolyline(Polyline polyline) {
        polyline.setEndCap(new RoundCap());
        polyline.setWidth((float) (POLYLINE_STROKE_WIDTH_PX));
        polyline.setColor(R.color.map_lines);
        polyline.setJointType(JointType.ROUND);
    }

    public void locationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(60000);
        mLocationRequest.setSmallestDisplacement(10f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //mLocationCallback.onLocationResult();

        mLocationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                List<Location> locationList=locationResult.getLocations();
                if(!locationList.isEmpty()){
                    Location location= locationList.get(locationList.size() - 1);
                    mLastLocation=location;
                    if (mCurrLocationMarker!=null)mCurrLocationMarker.remove();

                    LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng).title("Current Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                }
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.isMyLocationEnabled();
            } else {
                // Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.isMyLocationEnabled();
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsCampus.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                String[] permission = {(Manifest.permission.ACCESS_FINE_LOCATION)};
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Location Permission Needed");
                builder.setMessage("This app needs the Location permission, please accept to use location functionality");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MapsCampus.this, permission, MY_PERMISSIONS_REQUEST_LOCATION);

                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MapsCampus.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) { // If request is cancelled, the result arrays are empty.
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
                if (ContextCompat.checkSelfPermission(this, String.valueOf(permission)) == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mMap.isMyLocationEnabled();
                }
            } else {
                Helper.toast(this,"Permission Denied");
            }
        }
    }

    @Override
    public void onClick(int position) {
        if(mCurrLocationMarker!=null)mCurrLocationMarker.remove();
        LatLng ism = latLngList.get(position);
        MarkerOptions marker=new MarkerOptions().position(ism).title(names[position]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker=mMap.addMarker(marker);
        mCurrLocationMarker.showInfoWindow();
        //mMap.getUiSettings().setMapToolbarEnabled(false);
    }
}