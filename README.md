
Implement Callback Interface: IFetchLocation

    void getCurrentLocation(Location location); // Current location found
 
    void gpsNotEnable() ; // When GPS disabled by User


	-------------------------------------------------------------------------------------
	
	
	
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
	
	
	
	-------------------------------------------------------------------------------------
	
1. Get current location :-

   getLocation(IFetchLocation iFetchLocationObj); 

2. You can get a one-time location or continues(by Distance or Time or Both) location;

getLocation(boolean isContinuesLocation, float distanceInterval, long timeInterval, IFetchLocation iFetchLocationObj);


3. Please Stop location when you try to get continues location

  stopLocationTracking();



4. Add below code in Root Activity  when you access location in the fragment:


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);

        }
    }

