
# Android-CurrentLocation

Utility Module for easy access to the device location on Android

## Demo

<img src="https://github.com/kalpeshkumawat/CurrentLocation/raw/master/screen_shot_1.png?raw=true" height="450" width="280" /> |
<img src="https://github.com/kalpeshkumawat/CurrentLocation/raw/master/screen_shot_2.png?raw=true" height="450" width="280" /> |
<img src="https://github.com/kalpeshkumawat/CurrentLocation/raw/master/screen_shot_3.png?raw=true" height="450" width="280" />

## Installation

**Add this library to your project**

###### Declare the Gradle repository in your root build.gradle

allprojects &nbsp;{ <br /> 
   &nbsp; &nbsp;&nbsp;repositories &nbsp;{ <br />
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  maven { url "https://jitpack.io" } <br />
}

###### Declare the Gradle dependency in your app module's build.gradle

dependencies { <br /> 
    &nbsp; &nbsp;&nbsp;&nbsp;implementation 'com.github.kalpeshkumawat:CurrentLocation:1.0' <br /> 
}

## Add the required permissions in AndroidManifest.xml

**For fine location (GPS location):**

```
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

**For coarse location (network location):**

```
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## Usage

**GetLocationActivity / GetLocationFragment**

```
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
	    // Get Here Latitude and Longitude
        }

    }

    @Override
    public void gpsNotEnable() {
        Toast.makeText(this, "GPS not enabled", Toast.LENGTH_SHORT).show();
    }
}
```
		
**1. Get current location :-** 

   getLocation(IFetchLocation iFetchLocationObj);   <br /> <br />
   
   

**2. You can get a one-time location or continues(by Distance or Time or Both) location:-**  

getLocation(boolean isContinuesLocation, float distanceInterval, IFetchLocation iFetchLocationObj);

getLocation(boolean isContinuesLocation, long timeInterval, IFetchLocation iFetchLocationObj);

getLocation(boolean isContinuesLocation, float distanceInterval, long timeInterval, IFetchLocation iFetchLocationObj); <br /> <br />



**3. Please Stop location when you try to get continues location:-**  

  stopLocationTracking(); <br /> <br />



**4. Add below code in Root Activity  when you access location in the fragment:-**  


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);

        }
    }

