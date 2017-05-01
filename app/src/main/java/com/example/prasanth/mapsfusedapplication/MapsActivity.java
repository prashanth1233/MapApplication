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
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    private Button shwAddressBtn, showMapButton;
    private TextView addressTV;

    private double latitude, longitude;
    private MapViewFragment mapview;

    private Location location;
    private final int REQUEST_CODE = 2;

    private FrameLayout frameLayout;

    private long UPDATE_INTERVAL = 10 * 1000;
    private long FASTEST_INTERVAL = 2000;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        frameLayout = (FrameLayout) findViewById(R.id.mapFrame);

        shwAddressBtn = (Button) findViewById(R.id.addressBtn);
        showMapButton = (Button) findViewById(R.id.mapButton);
        addressTV = (TextView) findViewById(R.id.addressDisplayTV);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        shwAddressBtn.setOnClickListener(this);
        showMapButton.setOnClickListener(this);

    }

    private void callFragment() {
        mapview = new MapViewFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("latitudeValue", latitude);
        bundle.putDouble("longitudeValue", longitude);
        mapview.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.mapFrame, mapview).commit();

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

    }

    private void getCompleteAddressString(double latitude, double longitude) {
        String strAddress = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnAddress = addresses.get(0);
                StringBuilder addressAppend = new StringBuilder();
                for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                    addressAppend.append(returnAddress.getAddressLine(i)).append("\n");
                }
                strAddress = addressAppend.toString();
                addressTV.setText(strAddress);
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
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addressBtn:
                if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_CODE);
                } else {
                    Toast.makeText(this, "You have permissions", Toast.LENGTH_LONG).show();
                    location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        startLocationUpdates();
                        addressTV.setVisibility(View.VISIBLE);
                        frameLayout.setVisibility(View.GONE);
                        getCompleteAddressString(latitude, longitude);
                    } else
                        Toast.makeText(this, "Problem in Retreiving Location", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.mapButton:
                addressTV.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                callFragment();
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    } else {
                        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            startLocationUpdates();
                            addressTV.setVisibility(View.VISIBLE);
                            frameLayout.setVisibility(View.GONE);
                            getCompleteAddressString(latitude, longitude);
                        } else {
                            Toast.makeText(this, "Problem in Retreiving Location", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "permission not granted", Toast.LENGTH_LONG).show();
                }
        }
    }
}
