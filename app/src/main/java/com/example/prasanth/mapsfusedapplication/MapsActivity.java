package com.example.prasanth.mapsfusedapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Button shwAddressBtn;
    private TextView addressTV;

    private double latitude, longitude;

    private long UPDATE_INTERVAL = 10 * 1000;
    private long FASTEST_INTERVAL = 2000;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        shwAddressBtn = (Button) findViewById(R.id.addressBtn);
        addressTV = (TextView) findViewById(R.id.addressDisplayTV);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        shwAddressBtn.setOnClickListener(this);

        callFragment();
    }

    private void callFragment() {
        MapViewFragment mapview=new MapViewFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mapFrame,mapview);
        fragmentTransaction.commit();

    }

    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        if (googleApiClient != null) {
            googleApiClient.disconnect();

        }
        super.onStop();
    }

    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("Debug", "current Location:" + location.toString());
            /*callFragment();*/
           /* Toast.makeText(this, "Latitude location is" + latitude, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Longitude location is" + longitude, Toast.LENGTH_LONG).show();*/
           /* getCompleteAddressString(latitude, longitude);*/
        } else
            Toast.makeText(this, "Problem in Retreiving Location", Toast.LENGTH_LONG).show();
        startLocationUpdates();
    }

    private void getCompleteAddressString(double latitude, double longitude) {
        Log.i("========latitude======",latitude+"");
        Log.i("========longitude======",longitude+"");
        String strAddress = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude,1);
            if (addresses != null) {
              /*  Toast.makeText(this, "Address Found", Toast.LENGTH_LONG).show();*/
                Address returnAddress = addresses.get(0);
                StringBuilder addressAppend = new StringBuilder();
                for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                    addressAppend.append(returnAddress.getAddressLine(i)).append("\n");
                }
                strAddress = addressAppend.toString();
                addressTV.setText(strAddress);
              /*  Toast.makeText(this,"Address is"+strAddress,Toast.LENGTH_LONG).show();*/
            } else {
                Toast.makeText(this, "Address Not Found", Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startLocationUpdates() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);
    }


    @Override
    public void onConnectionSuspended(int connectionDetails) {
        if (connectionDetails == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected:Please Re-connect", Toast.LENGTH_LONG).show();
        } else if (connectionDetails == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network Lost.Please Re-connect", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connectin Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location locationChanged) {
        String location = "Updated location is:" + locationChanged.getLatitude() + "," + locationChanged.getLongitude();
        /*Toast.makeText(this, location, Toast.LENGTH_LONG).show();*/
    }

    @Override
    public void onClick(View view) {
        getCompleteAddressString(latitude, longitude);
    }
}
