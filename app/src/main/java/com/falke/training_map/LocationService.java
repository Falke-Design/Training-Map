/*
 * Copyright (c) 2016 Martin Pfeffer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.falke.training_map;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.falke.training_map.database.DbHelper;
import com.falke.training_map.util.Const;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.pepperonas.andbasx.system.SystemUtils;

/**
 * @author Martin Pfeffer (pepperonas)
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LocationService";

    public static final String LOCATION_BROADCAST = "location_broadcast";

    private static final int FASTEST_INTERVAL = 0;
    private static final int INTERVAL = 5000;
    private static final int INTERVAL_WHEN_CHARGING = 1000;


    private IBinder mBinder = new LocalBinder();

    private boolean mIsStopped = false;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private DbHelper mDb;


    private String mTourName = "";

    private int tourID = 0;


    @Override
    public IBinder onBind(Intent intent) {
        mTourName = intent.getStringExtra("tour_name");

        return mBinder;
    }


    public void run() {
        connectGoogleApiClient();

        mDb = new DbHelper(this);

        if (mTourName.equals("")) {
            mTourName = getString(R.string.unnamed_tour);
        }


        // mCurrentTourId = mDb.addTour(new Tour(mTourName, System.currentTimeMillis(), true, mTourNote));

    }


    private void connectGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Const.P_F_LOC) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Const.P_C_LOC) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "startLocationUpdates: Missing permission 'ACCESS_FINE_LOCATION'");
            return;
        }

        createLocationRequest();

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    private void createLocationRequest() {
        Log.d(TAG, "Creating location request...");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(SystemUtils.isCharging(this) ? INTERVAL_WHEN_CHARGING : INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Connecting GoogleApiClient SUCCESS.");

        startLocationUpdates();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "Connecting GoogleApiClient FAILED.");
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location.getLatitude() + "/" + location.getLongitude());

        if (mIsStopped) return;


        sendLocationToMap(location);

        if ((SystemUtils.isCharging(this) && mLocationRequest.getInterval() == INTERVAL)
                || (!SystemUtils.isCharging(this) && mLocationRequest.getInterval() == INTERVAL_WHEN_CHARGING)) {
            if (ActivityCompat.checkSelfPermission(this, Const.P_F_LOC) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Const.P_C_LOC) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            mLocationRequest = null;
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(SystemUtils.isCharging(this) ? INTERVAL_WHEN_CHARGING : INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(SystemUtils.isCharging(this) ? INTERVAL_WHEN_CHARGING : INTERVAL);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        try {
            mDb.insert_P(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), tourID);

            Toast.makeText(this, "NEuer Punkt" + location.getLatitude() + "   dd   " + location.getLongitude(), Toast
                    .LENGTH_SHORT);

           /* mDb.addPosition(new Position(location.getLatitude(), location.getLongitude(), location.getAltitude(),
                            location.getAccuracy(), location.getSpeed(), location.getTime(), rAngle, 0, mMaxGforce),
                    mCurrentTourId); */
        } catch (Exception e) {
            Log.e(TAG, "onLocationChanged: Error while adding position.");
        }

    }


    private void sendLocationToMap(Location location) {
        Intent intent = new Intent();
        intent.setAction(LOCATION_BROADCAST);

        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());

        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        try {

            mDb.close();
            // mDb.makeTourSummary(mCurrentTourId);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: Error while making summary.");
        }

        stopLocationUpdates();

        setStopped();

        super.onDestroy();
    }


    public void setStopped() {
        mIsStopped = true;
    }


    public void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "Stopping location updates failed: " + e.getMessage());
        }
        Log.d(TAG, "Location updates stopped.");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    public class LocalBinder extends Binder {

        public LocationService getServiceInstance() {
            return LocationService.this;
        }
    }


}