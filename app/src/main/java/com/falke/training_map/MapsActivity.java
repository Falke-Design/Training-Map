package com.falke.training_map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.falke.training_map.database.DbHelper;
import com.falke.training_map.database.Point;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pepperonas.andbasx.base.ToastUtils;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private boolean mCameraUntouched = true;


    private class LocationReceiver extends BroadcastReceiver {

        private static final String TAG = "LocationReceiver";


        @Override
        public void onReceive(Context arg0, Intent arg1) {
            double lat = arg1.getDoubleExtra("latitude", 0);
            double lng = arg1.getDoubleExtra("longitude", 0);

            ToastUtils.toastShort(lat + "/" + lng);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

            if (mMap != null) {
                mMap.moveCamera(cameraUpdate);
                if (mCameraUntouched) {
                    mCameraUntouched = false;
                    mMap.animateCamera(zoom);
                }
            } else Log.e(TAG, "onReceive: OUCH! Map is null");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mMap = mapFragment.getMap();
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LocationReceiver locationReceiver = new LocationReceiver();
        IntentFilter intentFilter = new IntentFilter(LocationService.LOCATION_BROADCAST);
        registerReceiver(locationReceiver, intentFilter);


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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        getPoints();
    }


    public void getPoints() {

        DbHelper db = new DbHelper(this);
        int count_p = db.count_P();
        List<Point> l = db.getAll_P();
        Log.d("Points", "" + count_p);

        for (int i = 0; i < count_p; i++) {
            Point point = l.get(i);
            double lat = Double.parseDouble(point.getLAT());
            double lng = Double.parseDouble(point.getLNG());
            LatLng sydney = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
            Log.d("Points", "Punkt " + i + "LAT" + point.getLAT() + " LNG" + point.getLNG());
        }
    }
}
