package com.app.location;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;


public class GetLocationFragment extends Fragment {

    private Dialog permissionDialog;
    private GoogleApiClient googleApiClient;
    private Dialog gpsDialog;
    private IFetchLocation iFetchLocation;

    private boolean isContinueFlag = false;
    private float distanceFlag = 0f;
    private long timeFlag = 0;


    // Continue Location information -  Distance, Time

    public void getLocation(boolean isContinuesLocation, float distanceInterval, long timeInterval, IFetchLocation iFetchLocationObj) {

        this.iFetchLocation = iFetchLocationObj;

        this.isContinueFlag = isContinuesLocation;
        this.distanceFlag = distanceInterval;
        this.timeFlag = timeInterval;


        iniUtils();
    }


    // Continue Location information -  Distance

    public void getLocation(boolean isContinuesLocation, float distanceInterval, IFetchLocation iFetchLocationObj) {

        this.iFetchLocation = iFetchLocationObj;

        this.isContinueFlag = isContinuesLocation;
        this.distanceFlag = distanceInterval;

        iniUtils();
    }


    // Continue Location information - Time

    public void getLocation(boolean isContinuesLocation, long timeInterval, IFetchLocation iFetchLocationObj) {

        this.iFetchLocation = iFetchLocationObj;

        this.isContinueFlag = isContinuesLocation;
        this.timeFlag = timeInterval;

        iniUtils();
    }


    // One Time Location information

    public void getLocation(IFetchLocation iFetchLocationObj) {

        this.iFetchLocation = iFetchLocationObj;


        iniUtils();
    }


    private void iniUtils() {


        gpsDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        gpsDialog.setContentView(R.layout.dialog_no_gps_on);
        gpsDialog.setCancelable(false);
        gpsDialog.setCanceledOnTouchOutside(false);

        permissionDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        permissionDialog.setContentView(R.layout.dialog_permission_layout);
        permissionDialog.setCancelable(false);
        permissionDialog.setCanceledOnTouchOutside(false);

        if (checkFineLocationPermission()) {

            // Permission already Granted

            if (checkGPSStatus()) {

                // GPS enabled already, Get User current Location

                getCurrentLocationLatLong();

            } else {

                // GPS disabled, Ask dialog to ON GPS

                enableGPS();
            }


        } else {

            // Ask Location Permission

            askPermissionDialogShow();
        }

    }


    private void askPermissionDialogShow() {

        TextView tv_gps_on = permissionDialog.findViewById(R.id.tv_allow);

        Utils.pushDownAnim(tv_gps_on, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLocationsWithPermissionCheck();

            }
        });

        permissionDialog.show();
    }


    private void showLocationsWithPermissionCheck() {

        TedPermission.with(getActivity())
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        if (permissionDialog != null && permissionDialog.isShowing()) {

                            permissionDialog.dismiss();

                        }

                        if (checkGPSStatus()) {

                            // GPS enabled already, Get User current Location

                            getCurrentLocationLatLong();

                        } else {

                            // GPS disabled, Ask dialog to ON GPS

                            enableGPS();
                        }

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                    }
                })
                .setDeniedTitle("Location Permission Required")
                .setDeniedCloseButtonText("Not Now")
                .setGotoSettingButtonText("OPEN SETTINGS")
                .setDeniedMessage("Turn on location to quickly check your current location. To enable, go to Settings and turn on Location Permission.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }


    private void enableGPS() {


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            errorToast();

                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            Task<LocationSettingsResponse> result =
                    LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());


            result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the
                                // user a dialog.
                                try {
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    resolvable.startResolutionForResult(
                                            getActivity(),
                                            LocationGooglePlayServicesProvider.REQUEST_CHECK_SETTINGS);


                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.

                                } catch (ClassCastException e) {
                                    // Ignore, should be an impossible error.

                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == LocationGooglePlayServicesProvider.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocationLatLong();

            }
            if (resultCode == Activity.RESULT_CANCELED) {

                openGpsDialog();

            }
        }

    }



    private void openGpsDialog() {


        if (gpsDialog != null && !gpsDialog.isShowing()) {


            TextView tv_gps_on = gpsDialog.findViewById(R.id.tv_gps_on);
            TextView tv_gps_on_not_now = gpsDialog.findViewById(R.id.tv_gps_on_not_now);



            Utils.pushDownAnim(tv_gps_on_not_now,new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    iFetchLocation.gpsNotEnable();


                    gpsDialog.dismiss();
                }
            });


            Utils.pushDownAnim(tv_gps_on, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    googleApiClient = null;

                    enableGPS();

                }
            });


            gpsDialog.show();

        }
    }

    private void getCurrentLocationLatLong() {


        if (gpsDialog != null && gpsDialog.isShowing()) {


            gpsDialog.dismiss();


        }


        Utils.showProgress(getActivity());


        if (isContinueFlag) {

            // Continue getting location


            LocationParams builder;


            if (timeFlag > 0 && distanceFlag > 0) {

                // Add Time and Distance

                builder = new LocationParams.Builder()
                        .setAccuracy(LocationAccuracy.HIGH)
                        .setDistance(distanceFlag)
                        .setInterval(timeFlag)
                        .build();




            } else if (timeFlag > 0) {

                // Add Time Interval

                builder = new LocationParams.Builder()
                        .setAccuracy(LocationAccuracy.HIGH)
                        .setInterval(timeFlag)
                        .build();




            } else if (distanceFlag > 0) {


                // Add Distance Internal

                builder = new LocationParams.Builder()
                        .setAccuracy(LocationAccuracy.HIGH)
                        .setDistance(distanceFlag)
                        .build();




            } else {

                // Do not Add Distance and Time Interval

                builder = new LocationParams.Builder().setAccuracy(LocationAccuracy.HIGH).build();



            }


            SmartLocation.with(getActivity()).location()
                    .config(builder)
                    .continuous()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {

                            Utils.hideProgress();

                            iFetchLocation.getCurrentLocation(location);


                        }
                    });

        } else {

            // Only one time get location


            LocationParams builder = new LocationParams.Builder()
                    .setAccuracy(LocationAccuracy.HIGH).build();





            SmartLocation.with(getActivity()).location()
                    .config(builder)
                    .oneFix()
                    .start(new OnLocationUpdatedListener() {
                        @Override
                        public void onLocationUpdated(Location location) {

                            Utils.hideProgress();

                            iFetchLocation.getCurrentLocation(location);


                        }
                    });
        }


    }


    /**************************************  Common Methods  ******************************************/

    private boolean checkFineLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    public boolean checkGPSStatus() {
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    private void errorToast() {

        Toast.makeText(getActivity(), "Sorry! Something went wrong, Please try later...", Toast.LENGTH_SHORT).show();
    }

    public void stopLocationTracking() {

        SmartLocation.with(getActivity()).location().stop();


    }



}
