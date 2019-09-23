package com.app.socialauth;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.app.location.GetLocationActivity;
import com.app.location.IFetchLocation;

public class MainActivity extends GetLocationActivity implements IFetchLocation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getLocation(this);


    }


    @Override
    public void getCurrentLocation(Location location) {


        if (location != null) {

            Toast.makeText(this, "Location Found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void gpsNotEnable() {
        Toast.makeText(this, "GPS not enabled", Toast.LENGTH_SHORT).show();
    }
}
