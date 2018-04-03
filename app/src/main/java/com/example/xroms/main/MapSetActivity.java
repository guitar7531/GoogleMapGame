package com.example.xroms.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class MapSetActivity extends FullScreenActivity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMarkerDragListener {


    private MobileServiceClient mClient;
    private MobileServiceTable<ToDoItem> mActionTable;

    private static final String TAG = MapSetActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    Marker self, a, b, l, r;
    private LatLng spb = new LatLng(59, 30);

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private String roomId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ...............................");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_set);
        setMContentView(findViewById(R.id.llContentMS));

        roomId = getIntent().getStringExtra("id");

        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
            requestWritePermission(this);


        Button start = findViewById(R.id.btnStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mClient = new MobileServiceClient(
                            "https://cowboysvsindians.azurewebsites.net",
                            MapSetActivity.this);
                    mActionTable = mClient.getTable(ToDoItem.class);
                    ToDoItem item = new ToDoItem();
                    item.setId(roomId);
                    item.setGameStarted(true);
                    item.setIsHost(true);
                    item.setName(roomId);
                    item.setaBase1(a.getPosition().latitude);
                    item.setaBase2(a.getPosition().longitude);
                    item.setbBase1(b.getPosition().latitude);
                    item.setbBase2(b.getPosition().longitude);
                    item.setrBorder1(r.getPosition().latitude);
                    item.setrBorder2(r.getPosition().longitude);
                    item.setlBorder1(l.getPosition().latitude);
                    item.setlBorder2(l.getPosition().longitude);
                    mActionTable.update(item);
                }
                catch (final Exception e) {
                    Log.e("error!", e.getMessage());
                }

                Intent myIntent = new Intent(MapSetActivity.this, MapsActivity.class);
                myIntent.putExtra("id", roomId);
                startActivity(myIntent);
            }
        });
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.mapSet);
        mapFragment.getMapAsync(this);
        updateMap();
    }


    private void updateMap() {
        Log.d(TAG, "Map update initiated .............");
        if (null != mCurrentLocation) {
            LatLng cur_loc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            self.setPosition(cur_loc);


            if (a == null) {
                a = mMap.addMarker(new MarkerOptions().position(new LatLng(cur_loc.latitude, cur_loc.longitude + 0.001))
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_base_a))
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLng(cur_loc));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                MapsActivity.cur = cur_loc;

                l = mMap.addMarker(new MarkerOptions().position(new LatLng(cur_loc.latitude+0.0003, cur_loc.longitude - 0.001))
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_angle_l))
                );
                MapsActivity.locBaseA = a.getPosition();
                MapsActivity.l = l.getPosition();
            }
            if(b == null){
                b = mMap.addMarker(new MarkerOptions().position(new LatLng(cur_loc.latitude, cur_loc.longitude - 0.001))
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_base_b)));

                r = mMap.addMarker(new MarkerOptions().position(new LatLng(cur_loc.latitude-0.0003, cur_loc.longitude + 0.001))
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_angle_r))
                );

                MapsActivity.locBaseB = b.getPosition();
                MapsActivity.r = r.getPosition();
            }
        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        self = googleMap.addMarker(new MarkerOptions()
                .position(spb)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_cowboy))
                .flat(true)
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(spb));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(6));
        googleMap.setOnMarkerDragListener( this);
    }

    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        } else
            mGoogleApiClient.connect();
    }

    protected void startLocationUpdates() {

        Log.d(TAG, "Location requsest sending..............: ");
        @SuppressLint("MissingPermission") PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateMap();
    }


    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }


    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static void requestWritePermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(context)
                    .setMessage("This app needs permission to use The phone Camera in order to activate the Scanner")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }
                    }).show();

        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if(Objects.equals(marker.getId(), a.getId())){
            MapsActivity.locBaseA = marker.getPosition();
        }
        if(Objects.equals(marker.getId(), b.getId())){
            MapsActivity.locBaseB = marker.getPosition();
        }
        if(Objects.equals(marker.getId(), l.getId())){
            MapsActivity.l = marker.getPosition();
        }
        if(Objects.equals(marker.getId(), r.getId())){
            MapsActivity.r = marker.getPosition();
        }
    }
}
