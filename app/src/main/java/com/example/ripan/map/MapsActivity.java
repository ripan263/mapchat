package com.example.ripan.map;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {
    private GoogleMap mMap;
    EditText editText;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Message message1 = new Message();
        message1.setMessage("Hello there!");
        message1.setDate(new Date());
        message1.setLocation("53.344405, -6.257325");

        Message message2 = new Message();

        message2.setMessage("Hi, how are you?");
        message2.setDate(new Date());
        message2.setLocation("53.344411, -6.257430");

        Messages.postMessage(message1);
        Messages.postMessage(message2);

        Messages.update();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editText = findViewById(R.id.editText);
        editText.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView textView, int i, KeyEvent keyEvent) {
                final String title = textView.getText().toString();

                // Place pin at current location.
                RunWithCurrentLocation(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess (Location location) {
                        if (location != null) {
                            LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            Marker marker = mMap.addMarker(new MarkerOptions().position(curLocation).title(title));
                            textView.setVisibility(View.GONE);
                            marker.showInfoWindow();
                        }
                    }
                });


                return true;
            }
        });
    }

    void PanCameraToCurrentLocation() {
        RunWithCurrentLocation(new OnSuccessListener<Location>() {
                                   @Override
                                   public void onSuccess (Location location) {
                                       if (location != null) {
                                           LatLng curLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                           mMap.animateCamera(CameraUpdateFactory.newLatLng(curLocation));
                                       }
                                   }
                               });
    }

    // Run a given function with the current location, asynchronously.
    void RunWithCurrentLocation(final OnSuccessListener<Location> f) {
        mFusedLocationClient.getLastLocation().
                addOnSuccessListener(this, f);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Handler handler= new Handler();
        // Add a marker in Sydney and move the camera
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        // Set the style of the map..
        if (googleMap.setMapStyle(new MapStyleOptions(
                getResources().getString(R.string.map_style)
        )));

        PanCameraToCurrentLocation();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_LONG).show();
        editText.setVisibility(View.VISIBLE);

        // Show keyboard
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);

        PanCameraToCurrentLocation();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        return false;
    }
}
