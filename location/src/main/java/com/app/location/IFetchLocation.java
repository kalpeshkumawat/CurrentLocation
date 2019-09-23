package com.app.location;


import android.location.Location;

public interface IFetchLocation {


    void getCurrentLocation(Location location);

    void gpsNotEnable() ;
}
