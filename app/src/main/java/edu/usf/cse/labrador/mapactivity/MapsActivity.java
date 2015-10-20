package edu.usf.cse.labrador.mapactivity;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices; //added



public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    protected GoogleApiClient mGoogleApiClient;
    private TextView  mLatitudeText, mLongitudeText;
    private LocationRequest mLocationRequest;

    //static final LatLng USF = new LatLng(28.061816, -82.411282);

    public static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        buildGoogleApiClient();

        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(50000).setFastestInterval(5000);

        mLatitudeText = (TextView) findViewById(R.id.textView1);
        mLongitudeText = (TextView) findViewById(R.id.textView2);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }



    //BUILD THE GOOGLE API CLIENT
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {

        mMap.setMyLocationEnabled(true);

    }


    //ADDED
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i(TAG, "Location services connected");
        Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if( mLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);

        }
        else {
            handleNewLocation(mLocation); 
        }

    }

    private void handleNewLocation(Location mLocation) {
        Log.d(TAG, "The location is currently: " + mLocation.toString());

        double currentLat = mLocation.getLatitude();
        double currentLng = mLocation.getLongitude();

        mLongitudeText.setText("Longitude: " + currentLng);
        mLatitudeText.setText("Latitude: " + currentLat);

        LatLng latlng1 = new LatLng(currentLat, currentLng);

        MarkerOptions options = new MarkerOptions().position(latlng1)
                .title("Current Location");

        mMap.addMarker(options);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng1));
    }


    //ADDED
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended. Reconnect...");
    }


    //ADDED
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //@Override
    //public void onLocationChanged(Location location) {
    //    handleNewLocation(location);
    //}

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
            mGoogleApiClient.disconnect();
        }
    }


}
