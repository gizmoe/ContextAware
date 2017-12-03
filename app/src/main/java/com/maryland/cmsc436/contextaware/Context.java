package com.maryland.cmsc436.contextaware;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

/**
 * Created by brianchung on 11/29/17.
 */

public class Context extends AppCompatActivity {

    private static final String IN_LOCATION_FENCE_KEY = "IN_LOCATION_FENCE_KEY";
    private static final String EXITING_LOCATION_FENCE_KEY = "EXITING_LOCATION_FENCE_KEY";
    private static final String ENTERING_LOCATION_FENCE_KEY = "ENTERING_LOCATION_FENCE_KEY";
    private static final String HEADPHONE_FENCE_KEY = "HEADPHONE_FENCE_KEY";
    private final String TAG = "Context";
    public static final int STATUS_IN = 0;
    public static final int STATUS_OUT = 1;
    public static final int STATUS_ENTERING = 2;
    public static final int STATUS_EXITING = 3;

    private LocationManager locationManager;
    private LocationListener listener;

    RelativeLayout mLayoutLocationFence;
    TextView mHeadphoneText;

    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mPendingIntent;
    private LocationFenceReceiver mLocationFenceReceiver;
    private GeoApiContext context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.context);
        configureLocationListener();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mLocationFenceReceiver = new LocationFenceReceiver();
        Intent intent = new Intent(LocationFenceReceiver.FENCE_RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAe9ppAcukWJ9qTTWEIX_OMLUkXd1UGQfw")
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerFences();
        registerReceiver(mLocationFenceReceiver, new IntentFilter(LocationFenceReceiver.FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFences();
        unregisterReceiver(mLocationFenceReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registerFences();
                } else {
                   Log.i(TAG, "Permission not Granted");
                }
            }
        }
    }

    private void registerFences() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            AwarenessFence inLocationFence = LocationFence.in(50.830951, -0.146978, 200, 1);
            AwarenessFence exitingLocationFence = LocationFence.exiting(50.830951, -0.146978, 200);
            AwarenessFence enteringLocationFence = LocationFence.entering(50.830951, -0.146978, 200);
            AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);

            Awareness.FenceApi.updateFences(
                    mGoogleApiClient,
                    new FenceUpdateRequest.Builder()
                            .addFence(IN_LOCATION_FENCE_KEY, inLocationFence, mPendingIntent)
                            .addFence(HEADPHONE_FENCE_KEY, headphoneFence, mPendingIntent)
                            .build())
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(status.isSuccess()) {
                                Log.i(TAG, "Fence was successfully registered.");
                            } else {
                                Log.e(TAG, "Fence could not be registered: " + status);
                            }
                        }
                    });
        }
    }

    private void unregisterFences() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(IN_LOCATION_FENCE_KEY)
                        .removeFence(EXITING_LOCATION_FENCE_KEY)
                        .removeFence(ENTERING_LOCATION_FENCE_KEY)
                        .removeFence(HEADPHONE_FENCE_KEY)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(TAG, "Fence was successfully unregistered.");

            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.e(TAG, "Fence could not be unregistered: " + status);

            }
        });
    }


    public void getLocationFromAddress(String strAddress) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context,strAddress).await();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Log.i(TAG, gson.toJson(results[0].geometry.location));
        } catch (Exception e) {
            Log.i(TAG, "Failed to get lat/lng of address " + e.getMessage());
        }
    }




    private void configureLocationListener(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Log.i(TAG,"Location: " + location.getLongitude() + " " + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }
    public void getLocation(View v) {
        //getLocationFromAddress("11714 Oakspine Court Ellicott City MD 21042");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (locationResult.getStatus().isSuccess()) {
                                Location location = locationResult.getLocation();
                                Log.i(TAG, "Location Lat: " + location.getLatitude());
                                Log.i(TAG, "Location Long: " + location.getLongitude());
                            } else {

                            }
                        }
                    });
        }
    }

    class LocationFenceReceiver extends BroadcastReceiver {

        public static final String FENCE_RECEIVER_ACTION =
                "LocationFenceReceiver.FENCE_RECEIVER_ACTION";

        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);
            if (TextUtils.equals(fenceState.getFenceKey(), IN_LOCATION_FENCE_KEY)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.i(TAG, "IN_LOCATION");
                        break;
                    case FenceState.FALSE:
                        break;
                    case FenceState.UNKNOWN:
                        Log.i(TAG, "HEADPHONE STATE: STATUS UNKNOWN");
                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), EXITING_LOCATION_FENCE_KEY)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.i(TAG, "EXITING LOCATION");
                        break;
                    case FenceState.FALSE:

                        break;
                    case FenceState.UNKNOWN:

                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), ENTERING_LOCATION_FENCE_KEY)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.i(TAG, "ENTERING LOCATION");
                        break;
                    case FenceState.FALSE:

                        break;
                    case FenceState.UNKNOWN:

                        break;
                }
            } else if (TextUtils.equals(fenceState.getFenceKey(), HEADPHONE_FENCE_KEY)) {

                switch(fenceState.getCurrentState()){
                    case FenceState.TRUE:
                        Log.i(TAG, "HEADPHONE IN");
                        break;
                    case FenceState.FALSE:
                        Log.i(TAG, "HEADPHONE NOT IN ");
                        break;
                    case FenceState.UNKNOWN:
                        break;
                }
            }
        }
    }

}