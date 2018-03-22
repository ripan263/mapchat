package com.example.ripan.map;

import android.Manifest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;

public class MapsActivity extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        Messages.MessagesObserver
{
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private LocationManager manager;
    private Marker currentLocationMarker;
    EditText editText;
    public static final int REQUEST_LOCATION_CODE = 99;
    private FusedLocationProviderClient mFusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        //setContentView(R.layout.activity_maps);


/*        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && !manager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ){
            buildAlertMessageNoGps();
        }
*/

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //showMyAlert("checking permissions now");
            //checkLocationPermission();
        }
        final LocationManager manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //startActivity(intent);
            Log.v("Map actibikdcsnxn","fhiifeshik");
        }
        //GOES IN JAMES NAVDRAWER TODO
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getContext().portFragmentManager()
        //        .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);



        editText = view.findViewById(R.id.editText);
        editText.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String title = editText.getText().toString();
                //editText.setVisibility(editText.GONE);
                if (!title.equals("")) {
                    /*RunWithCurrentLocation(location -> {
                        if (location != null) {

                            LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            // Testing pintype, TODO: Load from ui..
                            Message.PinType randomType = Message.PinType.values()[new Random().nextInt(Message.PinType.values().length)];

                            Message m = new Message("TestUsername", title, curLocation, new Date(), randomType);

                            showMessageOnMap(m);
                            Messages.getInstance().postMessage(m);
                        }
                    })*/;
                    editText.setText("");
                }
            }
        });


        /*editText.setOnEditorActionListener((textView, i, keyEvent) -> {

            // Only respond to key up events.
            if (keyEvent.getAction() != KeyEvent.ACTION_UP)
                return true;

            //final String title = textView.getText().toString();
            Log.v("MapsActivity", "OnEditorAction" + keyEvent);

            //textView.setVisibility(View.GONE);
            textView.setText("");

            // Place pin at current location.
            RunWithCurrentLocation(location -> {
                if (location != null) {

                    LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    Message m = new Message("TestUsername", title, curLocation, new Date());
                    // Display on map.
                    Messages.displayMsgOnMap(m);
                    Messages.postMessage(m);
                }
            });

            return true;
        });*/

        Messages.getInstance().update();

        return view;
    }
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        manager = (LocationManager) getContext().getSystemService( Context.LOCATION_SERVICE );
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        super.onCreate(savedInstanceState);

    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    {
                        if(client ==null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);

                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        lastLocation = location;

        if(currentLocationMarker != null)
        {
            currentLocationMarker.remove();
        }
        Log.d("lat = ",""+latitude);
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions =new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        //currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));


        if(client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }
 /*
    void PanCameraToCurrentLocation() {
        RunWithCurrentLocation(location -> {
            if (location != null) {
                LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(curLocation));
            }
        });
    }
*/
    // Run a given function with the current location, asynchronously.
  /*
    void RunWithCurrentLocation(final OnSuccessListener<Location> f) {
        if (checkLocationPermission()) {
            mFusedLocationClient.getLastLocation().
                    addOnSuccessListener(getActivity(), f);
        }
    }*/


    /**
     * Manipulates the map once available.
     * getContext() callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. getContext() method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add listener to messages.
        Messages.getInstance().addObserver(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                    mMap.setOnMyLocationButtonClickListener(this);
                    mMap.setOnMyLocationClickListener(this);
                }
                else{}
        }

        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
        // Set the style of the map..
        if (googleMap.setMapStyle(new MapStyleOptions(
                getResources().getString(R.string.map_style)
        )));

        //PanCameraToCurrentLocation();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_LONG).show();
        editText.setVisibility(View.VISIBLE);

        // Show keyboard
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);

        //PanCameraToCurrentLocation();
    }
    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();

    }

    public void acceptNewMessages(ArrayList<Message> messages) {
        for (Message m : messages) {
           showMessageOnMap(m);
        }
    }

    private void showMessageOnMap(Message m) {
        // TODO: use pintype.
        Message.PinType t = m.getPinType();

        Marker marker = mMap.addMarker(new MarkerOptions().position(m.getLocation()).title(m.getUserID() + ": " + m.getMessage()));

        marker.showInfoWindow();
    }

    // permission for state of GPS  and turning it on
   /* public void turnGPSOn()
    {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        getContext().sendBroadcast(intent);

        String provider = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getContext().sendBroadcast(poke);


        }
    }
    // automatic turn off the gps
    public void turnGPSOff()
    {
        String provider = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getContext().sendBroadcast(poke);
        }
    }*/
/*
    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){

                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else return true;

    }
    */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        System.exit(0);
                        //getContext().finishAffinity();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        return false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume(){
        super.onResume();
/*        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && !manager.isProviderEnabled( LocationManager.NETWORK_PROVIDER ) ){
            buildAlertMessageNoGps();
        }
*/
    }

}
