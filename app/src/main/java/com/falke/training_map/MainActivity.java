package com.falke.training_map;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.falke.training_map.database.DbHelper;
import com.falke.training_map.util.Const;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends Activity {

    TextView tv;
    int i = 0;

    DbHelper db;
    Intent myservice;
    int count = 0;


    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;

    private float m_rAngleRaw = 0f;
    private float m_rAngleCalibration = 0f;

    private boolean mIsTracking = false;

    private LocationService mLocationService;






    private boolean mBounded;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);

        db = new DbHelper(this);

        count = db.rowcount();
        tv.setText("" + count);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        ensurePermissions();


        Button plus = (Button) findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                count++;
                tv.setText("" + count);
                db.addPrid(count);
                db.insert_R("Plus Row");
                Toast.makeText(MainActivity.this, "Neue Tabelle", Toast.LENGTH_SHORT).show();

            }
        });

        Button auf = (Button) findViewById(R.id.auf);
        auf.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!mIsTracking) {
                    mIsTracking = true;
                    launchService();
                }

            }
        });

        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(mIsTracking) {
                    mIsTracking = false;
                    stopService();
                }

            }
        });

        Button map = (Button) findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent maps = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(maps);

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onResume() {
        super.onResume();
        ensureWarningIfMissingGps();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy ");

        stopService();

        super.onDestroy();
    }

    private void ensureWarningIfMissingGps() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            new AlertDialog.Builder(this)
                    .setTitle("Delete entry")
                    .setMessage(R.string.dialog_gps_inactive_message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            mLocationService.stopLocationUpdates();
            mLocationService = null;
        }


        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            LocationService.LocalBinder mLocalBinder = (LocationService.LocalBinder) service;
            mLocationService = mLocalBinder.getServiceInstance();
            mLocationService.run();
        }
    };


    private void ensurePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_perm_req_location_title)
                        .setMessage(R.string.dialog_perm_req_location_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_FINE_LOCATION);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission 'ACCESS_FINE_LOCATION' granted");

                    stopService();
                    launchService();

                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.dialog_perm_req_location_failed_title)
                            .setMessage(R.string.dialog_perm_req_location_failed_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    requestPermissions(new String[]{Const.P_F_LOC}, PERMISSION_REQUEST_FINE_LOCATION);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }
    }


    public void launchService() {
        Intent intent = new Intent(this, LocationService.class);
        String tourName = "";
        String tourNote = "";

        intent.putExtra("r_angle_calibration", m_rAngleCalibration);
        intent.putExtra("tour_name", "test");
        intent.putExtra("tour_note", "testnote");
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }


    private void stopService() {
        if (mBounded) {
            unbindService(mServiceConnection);
            mBounded = false;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

