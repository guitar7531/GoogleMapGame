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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.abs;

public class MapsActivity extends FullScreenActivity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {


    private String roomId;
    private String yourname;

    private MobileServiceClient mClient;

    private MobileServiceTable<ToDoItem> mActionTable;

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    public static LatLng locBaseA, locBaseB, cur, l, r;
    Marker a, b, self;
    HashMap<String, Marker> players;
    Button flag;

    private void onMarkersRefresh(String name, LatLng loc){
        Marker v = players.get(name);
        v.setPosition(loc);
    }

    private void onNewMarker(String name, LatLng loc){
        Marker val = mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromResource(R.drawable.cowboy1)));
        players.put(name, val);
    }

    private void onMarkersResresh(List<ToDoItem> markers){
        for(ToDoItem i : markers){
            if(!players.containsKey(i.getName())) onNewMarker(i.getName(), new LatLng(i.getPosition1(), i.getPosition2()));
            else onMarkersRefresh(i.getName(), new LatLng(i.getPosition1(), i.getPosition2()));
        }
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ...............................");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setMContentView(findViewById(R.id.map));

        roomId = getIntent().getStringExtra("id");
        yourname = getIntent().getStringExtra("name");

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://cowboysvsindians.azurewebsites.net",
                    MapsActivity.this);

            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(5, TimeUnit.SECONDS);
                    client.setWriteTimeout(5, TimeUnit.SECONDS);
                    return client;
                }
            });
        } catch (Exception e) {
            Log.e("tagged", e.getMessage());
        }

        flag = findViewById(R.id.btnflag);
        Log.e("tagged", "kept you");
        // Get the Mobile Service Table instance to use
        mActionTable = mClient.getTable(ToDoItem.class);
        Log.e("tagged", "omg");
        setstart();
        Log.e("tagged", "@@@@@");

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        Log.e("tagged", "aaaa");
        createLocationRequest();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        Log.e("tagged", "huh");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.e("tagged", "waiting");
        updateMap();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.e("qq", e.getMessage());
                    }
                    refreshItemsFromTable();
                }
            }
        }).start();
    }


    private List<ToDoItem> refreshPlayers() throws ExecutionException, InterruptedException {
        return mActionTable.where().field("id").not().eq(yourname).and(mActionTable.where().field("name").eq(roomId)).execute().get();
    }

    private void setstart() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    List<ToDoItem> q = mActionTable.where().field("id").eq(roomId).execute().get();
                    Log.e("tagged", Integer.toString(q.size()));
                    ToDoItem roomholder = q.get(0);
                    locBaseA = new LatLng(roomholder.getaBase1(), roomholder.getaBase2());
                    locBaseB = new LatLng(roomholder.getbBase1(), roomholder.getbBase2());
                    l = new LatLng(roomholder.getlBorder1(), roomholder.getlBorder2());
                    r = new LatLng(roomholder.getrBorder1(), roomholder.getrBorder2());
                } catch (Exception e) {
                    Log.e("tagged", e.getMessage());

                }

                return null;
            }
        };

        runAsyncTask(task);
    }


    private void refreshItemsFromTable() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<ToDoItem> results = refreshPlayers();
                    //Log.e("tagged1", Integer.toString(results.size()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onMarkersResresh(results);
                        }
                    });
                } catch (final Exception e){
                    Log.e("tagged", e.getMessage());
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private void updateMap() {
        Log.d(TAG, "Map update initiated .............");
        if (null != mCurrentLocation) {
            updatePosition();
            LatLng cur_loc = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            if(self == null) self = mMap.addMarker(new MarkerOptions().position(cur_loc).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_self_a)));
            else self.setPosition(cur_loc);

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

        a = mMap.addMarker(new MarkerOptions()
                .position(locBaseA)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_base_a))
        );
        b = mMap.addMarker(new MarkerOptions()
                .position(locBaseB)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_base_b))
        );
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cur,18));
        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(r.latitude,l.longitude), new LatLng(l.latitude,r.longitude)));
    }


    private void updatePosition() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    ToDoItem newitem =  new ToDoItem();
                    newitem.setName(roomId);
                    newitem.setId(yourname);
                    newitem.setPosition1(mCurrentLocation.getLatitude());
                    newitem.setPosition2(mCurrentLocation.getLongitude());
                    mActionTable.update(newitem);
                } catch (final Exception e){
                    Log.e( "tagged", "error2");
                }

                return null;
            }
        };

        runAsyncTask(task);

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
        Log.d(TAG, "Location update started ...............: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        double l = location.getLatitude() - a.getPosition().latitude;
        double lt = location.getLongitude() - a.getPosition().longitude;
        if(abs(l)<0.0001 && abs(lt) < 0.0001){
            flag.setVisibility(View.VISIBLE);
            flag.setClickable(true);
        }
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
        Log.d(TAG, "onConnected - isConnected ................: " + mGoogleApiClient.isConnected());
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


}
